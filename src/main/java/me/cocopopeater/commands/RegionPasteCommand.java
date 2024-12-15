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
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        FabricClientCommandSource source = context.getSource();



        if(!BlockUtils.canSetBlocks()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("You dont have permission to perform this command")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        SchematicRegion clipboardRegion = PlayerVariableManager.getSchematicRegion();
        if(clipboardRegion == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("You do not have a region copied").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        PlayerUtils.sendCommandList(clipboardRegion.generateFillCommands());
        return 1;
    }
}
