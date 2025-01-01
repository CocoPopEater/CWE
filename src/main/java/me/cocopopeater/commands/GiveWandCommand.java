package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GiveWandCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/wand").executes(GiveWandCommand::run));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        PlayerUtils.sendCommandAsPlayer("give %s wooden_axe".formatted(MinecraftClient.getInstance().player.getName().getString()));
        return 1;
    }
}
