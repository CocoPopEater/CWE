package me.cocopopeater.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.config.FileManager;
import me.cocopopeater.regions.ClipboardRegion;
import me.cocopopeater.util.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SchematicCommand {

    private static SuggestionProvider<FabricClientCommandSource> generateSchematicList(){
        SuggestionProvider<FabricClientCommandSource> OPTION_SUGGESTIONS = (context, builder) -> {
            builder.suggest("list");
            builder.suggest("save");
            builder.suggest("load");
            return builder.buildFuture();
        };
        return OPTION_SUGGESTIONS;
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess){
        dispatcher.register(literal("/schematic")
                        .then(argument("option", StringArgumentType.string()).suggests(generateSchematicList()).executes(SchematicCommand::runList)
                                .then(argument("schematic-name", StringArgumentType.string()).executes(SchematicCommand::runSchematic))));
    }

    public static int runList(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String option = context.getArgument("option", String.class);

        if(!(option.equalsIgnoreCase("list"))){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Missing argument")
            );
            return 0;
        }
        List<String> schematicList = FileManager.getSchematicList();
        StringBuilder sb = new StringBuilder();
        sb.append("Saved Schematics:");
        sb.append("\n");
        for(String s : schematicList){
            sb.append(s);
            sb.append("\n");
        }
        Text message = Text.literal(sb.toString());
        PlayerUtils.sendPlayerMessageChat(
                message
        );
        return 1;
    }

    public static int runSchematic(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("The mod is not enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String option = context.getArgument("option", String.class);
        String schematicName = context.getArgument("schematic-name", String.class);
        if(option.equalsIgnoreCase("list")){
            runList(context);
            return 1;
        }else {
            if(schematicName == null || schematicName.isEmpty()){
                PlayerUtils.sendPlayerMessageChat(
                        Text.literal("Please specify schematic name").withColor(GlobalColorRegistry.getBrightRed())
                );
                return 0;
            }
            if(option.equalsIgnoreCase("save")){
                if(PlayerVariableManager.getPos1() == null || PlayerVariableManager.getPos2() == null){
                    PlayerUtils.sendPlayerMessageChat(
                            Text.literal("You need to set the region positions").withColor(GlobalColorRegistry.getBrightRed())
                    );
                    return 0;
                }
                ClipboardRegion region = PlayerVariableManager.createClipboardRegion();
                FileManager.saveSchematic(region, schematicName);
                return 1;
            } else if(option.equalsIgnoreCase("load")){
                ClipboardRegion region = FileManager.loadSchematic(schematicName);
                if(region == null){
                    return 0;
                }
                PlayerVariableManager.setClipboardRegion(region);
            }else{
                PlayerUtils.sendPlayerMessageChat(
                        Text.literal("Unknown argument: %s".formatted(option)).withColor(GlobalColorRegistry.getBrightRed())
                );
            }
        }

        return 1;
    }

}
