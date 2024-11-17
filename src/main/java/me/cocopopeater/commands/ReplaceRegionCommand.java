package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.TimeUnit;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.registry.Registries.BLOCK;

public class ReplaceRegionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/replace")
                .then(argument("fromBlock", StringArgumentType.string())
                        .then(argument("toBlock", StringArgumentType.string())
                                .executes(ReplaceRegionCommand::run))));
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

        String fromBlock = context.getArgument("fromBlock", String.class);
        String toBlock = context.getArgument("toBlock", String.class);

        var fromBlockCheck = BLOCK.getOptionalValue(Identifier.ofVanilla(fromBlock));
        var toBlockCheck = BLOCK.getOptionalValue(Identifier.ofVanilla(toBlock));

        if (fromBlockCheck.isEmpty()) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unknown block: %s".formatted(fromBlock)).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if (toBlockCheck.isEmpty()) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unknown block: %s".formatted(toBlock)).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        CuboidRegion master = PlayerVariableManager.getCuboid();
        TimedCommandRunner runner = new TimedCommandRunner();
        for(CuboidRegion subRegion : master.separateRegion()){
            runner.addTask(() -> subRegion.replace(fromBlock, toBlock));
        }

        runner.start(ConfigHandler.getInstance().getCommandDelay(), TimeUnit.MILLISECONDS);

        return 1;
    }
}
