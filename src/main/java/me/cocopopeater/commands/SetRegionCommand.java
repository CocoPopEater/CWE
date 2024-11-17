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

        TimedCommandRunner runner = new TimedCommandRunner();
        for(CuboidRegion subRegion : master.separateRegion()){
            runner.addTask(() -> subRegion.fill(blockName));
        }

        PlayerUtils.sendPlayerMessageChat(
                Text.literal("Workload: %d blocks".formatted(master.getTotalBlocks()))
        );

        runner.start(ConfigHandler.getInstance().getCommandDelay(), TimeUnit.MILLISECONDS);

        return 1;
    }
}
