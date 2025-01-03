package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.regions.CuboidRegion;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ContractRegionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/contract")
                .then(argument("amount", IntegerArgumentType.integer())
                        .then(argument("direction", StringArgumentType.string())
                                .executes(ContractRegionCommand::run)
                        )
                )

                .then(argument("direction", StringArgumentType.string())
                        .executes(ContractRegionCommand::runNoQty)
                )

        );
    }

    public static int runNoQty(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String direction = context.getArgument("direction", String.class);

        CuboidRegion start = PlayerVariableManager.getCuboid();
        PlayerVariableManager.contract(1, direction);
        CuboidRegion end = PlayerVariableManager.getCuboid();

        int blockCountDiff = Math.abs(start.getTotalBlocks() - end.getTotalBlocks());

        Text msg = Text.translatable("command.contract_region.contracted", blockCountDiff).withColor(GlobalColorRegistry.getLimeGreen());
        PlayerUtils.sendPlayerMessageChat(msg);

        return 1;
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        int amount = context.getArgument("amount", Integer.class);
        String direction = context.getArgument("direction", String.class);

        CuboidRegion start = PlayerVariableManager.getCuboid();
        PlayerVariableManager.contract(amount, direction);
        CuboidRegion end = PlayerVariableManager.getCuboid();

        int blockCountDiff = Math.abs(start.getTotalBlocks() - end.getTotalBlocks());

        Text msg = Text.translatable("command.contract_region.contracted", blockCountDiff).withColor(GlobalColorRegistry.getLimeGreen());
        PlayerUtils.sendPlayerMessageChat(msg);

        return 1;
    }
}
