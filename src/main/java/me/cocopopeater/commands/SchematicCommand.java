package me.cocopopeater.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.cocopopeater.regions.SchematicRegion;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.config.FileManager;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SchematicCommand {

    private static SuggestionProvider<FabricClientCommandSource> generateSchematicList(){
        return (context, builder) -> {
            builder.suggest("list");
            builder.suggest("save");
            builder.suggest("load");
            return builder.buildFuture();
        };
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess){
        dispatcher.register(literal("/schematic")
                        .then(argument("option", StringArgumentType.string()).suggests(generateSchematicList()).executes(SchematicCommand::runList)
                                .then(argument("schematic-name", StringArgumentType.string()).executes(SchematicCommand::runSchematic))
                        )
        );
    }

    public static int runList(CommandContext<FabricClientCommandSource> context){
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String option = context.getArgument("option", String.class);

        if(!(option.equalsIgnoreCase("list"))){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("command.error.missing_argument")
            );
            return 0;
        }
        List<String> schematicList = FileManager.getSchematicList();
        StringBuilder sb = new StringBuilder();
        sb.append(Text.translatable("mod.ui.saved_schematics").getString());
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
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
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
                        Text.translatable("command.schematic.specify_schematic_name").withColor(GlobalColorRegistry.getBrightRed())
                );
                return 0;
            }
            if(option.equalsIgnoreCase("save")){
                if(PlayerVariableManager.getPos1() == null || PlayerVariableManager.getPos2() == null){
                    PlayerUtils.sendPlayerMessageChat(
                            Text.translatable("region.positions_required").withColor(GlobalColorRegistry.getBrightRed())
                    );
                    return 0;
                }
                CompletableFuture.supplyAsync(() -> PlayerVariableManager.createSchematicRegion()).thenAcceptAsync(region -> FileManager.saveSchematic(region, schematicName));
                return 1;
            } else if(option.equalsIgnoreCase("load")){
                CompletableFuture<SchematicRegion> future = CompletableFuture.supplyAsync(() -> FileManager.loadSchematic(schematicName));
                future.thenAccept(region -> {
                    if(region == null){
                        PlayerUtils.sendPlayerMessageChat(
                                Text.translatable("command.schematic.invalid_schematic").withColor(GlobalColorRegistry.getBrightRed())
                        );
                    }else{
                        PlayerVariableManager.setSchematicRegion(region);
                        PlayerUtils.sendPlayerMessageChat(
                                Text.translatable("command.schematic.schematic_loaded", schematicName)
                                        .withColor(GlobalColorRegistry.getLimeGreen())
                        );
                    }
                });

            }else{
                PlayerUtils.sendPlayerMessageChat(
                        Text.translatable("command.error.invalid_argument", option).withColor(GlobalColorRegistry.getBrightRed())
                );
            }
        }

        return 1;
    }

}
