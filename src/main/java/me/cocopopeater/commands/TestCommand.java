package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TestCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/test")
                .executes(TestCommand::run)
                .then(literal("print").executes(TestCommand::runPrint)));
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        return 1;
    }

    private static int runPrint(CommandContext<FabricClientCommandSource> context){
        return 1;
    }


}
