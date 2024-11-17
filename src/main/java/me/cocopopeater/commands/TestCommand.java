package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.util.BlockUtils;
import me.cocopopeater.util.PlayerUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TestCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/test")
                .executes(TestCommand::run));
    }

    private static int run(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        PlayerUtils.sendPlayerMessageChat(
                Text.literal(String.valueOf(BlockUtils.canSetBlocks()))
        );
        return 1;
    }


}
