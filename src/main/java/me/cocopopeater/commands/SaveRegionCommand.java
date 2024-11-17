package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.config.FileManager;
import me.cocopopeater.regions.ClipboardRegion;
import me.cocopopeater.util.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SaveRegionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/save")
                .then(argument("region-name", StringArgumentType.string()).executes(SaveRegionCommand::run)));

    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        if(PlayerVariableManager.getPos1() == null || PlayerVariableManager.getPos2() == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("You need to set the region positions").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String regionName = context.getArgument("region-name", String.class);
        if(regionName == null || regionName.isEmpty()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Please specify a name for the region").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        ClipboardRegion region = PlayerVariableManager.createClipboardRegion();
        FileManager.saveRegion(region, regionName);
        return 1;
    }
}
