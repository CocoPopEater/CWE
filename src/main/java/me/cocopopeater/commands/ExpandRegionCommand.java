package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ExpandRegionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/expand")
                .then(argument("amount", IntegerArgumentType.integer())
                        .then(argument("direction", StringArgumentType.string())
                                .executes(ExpandRegionCommand::run))));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        int amount = context.getArgument("amount", Integer.class);
        String direction = context.getArgument("direction", String.class);

        CuboidRegion start = PlayerVariableManager.getCuboid();
        PlayerVariableManager.expand(amount, direction);
        CuboidRegion end = PlayerVariableManager.getCuboid();

        int blockCountDiff = Math.abs(start.getTotalBlocks() - end.getTotalBlocks());

        Text msg = Text.literal("Region expanded %d blocks".formatted(blockCountDiff)).withColor(GlobalColorRegistry.getLimeGreen());
        PlayerUtils.sendPlayerMessageChat(msg);

        return 1;
    }
}
