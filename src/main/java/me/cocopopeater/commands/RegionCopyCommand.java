package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RegionCopyCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/copy").executes(RegionCopyCommand::run));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if(PlayerVariableManager.getPos1() == null || PlayerVariableManager.getPos2() == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("region.positions_required").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        PlayerVariableManager.saveToClipboard();
        PlayerUtils.sendPlayerMessageChat(
                Text.translatable("region.copied").withColor(GlobalColorRegistry.getLimeGreen())
        );
        return 1;
    }
}
