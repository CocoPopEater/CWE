package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.regions.SchematicRegion;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.*;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RegionPasteCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/paste")
                .executes(RegionPasteCommand::run));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if(!BlockUtils.canSetBlocks()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.generic.insufficient_permission")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        SchematicRegion clipboardRegion = PlayerVariableManager.getSchematicRegion();
        if(clipboardRegion == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("region.no_region_copied").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        PlayerUtils.sendCommandList(clipboardRegion.generateFillCommands());
        return 1;
    }
}
