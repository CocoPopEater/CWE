package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.blocks.Zone;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ChunkCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/chunk")
                .executes(ChunkCommand::run)
                .then(literal("-s")
                        .executes(ChunkCommand::runIntersect)
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        WorldChunk chunk = player.getWorld().getWorldChunk(player.getBlockPos());

        int minX = chunk.getPos().x * 16;
        int minZ = chunk.getPos().z * 16;

        int maxX = minX + 15;
        int maxZ = minZ + 15;

        SimpleBlockPos minPos = new SimpleBlockPos(minX, PlayerUtils.getPlayerCurrentWorldMinHeight(), minZ);
        SimpleBlockPos maxPos = new SimpleBlockPos(maxX, PlayerUtils.getPlayerCurrentWorldMaxHeight(), maxZ);

        PlayerVariableManager.setPos1(minPos.toBlockPos());
        PlayerVariableManager.setPos2(maxPos.toBlockPos());
        return 1;
    }

    private static int runIntersect(CommandContext<FabricClientCommandSource> context){
        List<WorldChunk> intersectingChunks = new ArrayList<>();
        Zone selection = new Zone(SimpleBlockPos.fromBlockPos(PlayerVariableManager.getPos1()), SimpleBlockPos.fromBlockPos(PlayerVariableManager.getPos2()));
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        World world = player.getWorld();
        for(SimpleBlockPos pos : selection.getAllPoints()){
            WorldChunk chunk = world.getWorldChunk(pos.toBlockPos());
            boolean chunkExists = intersectingChunks.stream()
                    .anyMatch(c -> c.getPos().x == chunk.getPos().x && c.getPos().z == chunk.getPos().z);

            if(!chunkExists){
                intersectingChunks.add(chunk);
            }

        }

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for(WorldChunk chunk : intersectingChunks){
            int chunkMinX = chunk.getPos().x * 16;
            int chunkMinZ = chunk.getPos().z * 16;

            int chunkMaxX = chunkMinX + 15;
            int chunkMaxZ = chunkMinZ + 15;

            if(chunkMinX < minX) minX = chunkMinX;
            if(chunkMinZ < minZ) minZ = chunkMinZ;

            if(chunkMaxX > maxX) maxX = chunkMaxX;
            if(chunkMaxZ > maxZ) maxZ = chunkMaxZ;
        }
        SimpleBlockPos min = new SimpleBlockPos(minX, PlayerUtils.getPlayerCurrentWorldMinHeight(), minZ);
        SimpleBlockPos max = new SimpleBlockPos(maxX, PlayerUtils.getPlayerCurrentWorldMaxHeight(), maxZ);

        PlayerVariableManager.setPos1(min.toBlockPos());
        PlayerVariableManager.setPos2(max.toBlockPos());
        return 1;
    }


}
