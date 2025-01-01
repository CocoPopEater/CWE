package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.BlockUtils;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.RegionUtils;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.registry.Registries.BLOCK;

public class ReplaceNearCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/replacenear")
                .then(argument("radius", IntegerArgumentType.integer())
                    .then(argument("fromBlock", StringArgumentType.string())
                            .then(argument("toBlock", StringArgumentType.string())
                                    .executes(ReplaceNearCommand::run)
                            )
                    )
                )
        );
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if(!BlockUtils.canFillBlocks()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.generic.insufficient_permission")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        String fromBlock = context.getArgument("fromBlock", String.class);
        String toBlock = context.getArgument("toBlock", String.class);
        int radius = context.getArgument("radius", Integer.class);

        var fromBlockCheck = BLOCK.getOptionalValue(Identifier.ofVanilla(fromBlock));
        var toBlockCheck = BLOCK.getOptionalValue(Identifier.ofVanilla(toBlock));

        if (fromBlockCheck.isEmpty()) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.blocks.unknown_block", fromBlock).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if (toBlockCheck.isEmpty()) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.blocks.unknown_block", toBlock).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        CuboidRegion master = RegionUtils.createCuboidAroundPoint(SimpleBlockPos.fromBlockPos(context.getSource().getPlayer().getBlockPos()), radius);

        List<String> commands = new ArrayList<>();

        for(CuboidRegion subRegion : master.separateRegion()){
            commands.add(subRegion.getReplaceCommand(fromBlock, toBlock));
        }

        PlayerUtils.sendCommandList(commands);

        return 1;
    }
}
