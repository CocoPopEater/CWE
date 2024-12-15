package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.*;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.registry.Registries.BLOCK;

public class SetRegionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/set")
                .then(argument("block", StringArgumentType.greedyString()
                ).executes(SetRegionCommand::run)));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if(!BlockUtils.canFillBlocks()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("You dont have permission to perform this command")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String blockName = context.getArgument("block", String.class);

        var blockCheck = BLOCK.getOptionalValue(Identifier.ofVanilla(blockName));

        if (blockCheck.isEmpty()) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unknown block: %s".formatted(blockName)).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        CuboidRegion master = PlayerVariableManager.getCuboid();

        List<String> commands = new ArrayList<>();

        for(CuboidRegion subRegion : master.separateRegion()){
            commands.add(subRegion.getFillCommand(blockName));
        }

        PlayerUtils.sendCommandList(commands);

        return 1;
    }
}
