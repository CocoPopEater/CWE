package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.tools.TreeType;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.tools.ToolType;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.BlockItem;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ToolsCommand {


    private static final SuggestionProvider<FabricClientCommandSource> DYNAMIC_SUGGESTIONS = (context, builder) -> {
        if(context.getNodes().size() > 1){
            // the user has types a tool type
            String toolType = context.getArgument("tool-name", String.class);

            switch(toolType.toLowerCase()){
                case "tree" -> {
                    for(String treeType : TreeType.getCleanValues()){
                        builder.suggest(treeType);
                    }
                    return builder.buildFuture();
                }
            }
        } else{
            // the user has yet to type a tool type
            for(String tool : ToolType.getTools()){
                builder.suggest(tool);
            }
            return builder.buildFuture();
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/tool")
                        .executes(ToolsCommand::run)
                .then(ClientCommandManager.argument("tool-name", StringArgumentType.string())
                        .suggests(DYNAMIC_SUGGESTIONS) // suggestions for tool name
                        .executes(ToolsCommand::runChoice)
                        .then(ClientCommandManager.argument("tool-info", StringArgumentType.string())
                                .suggests(DYNAMIC_SUGGESTIONS)
                                .executes(ToolsCommand::runChoice)
                        )// suggestions for tree type
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        if(!ConfigHandler.getInstance().isEnabled()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("mod.status.not_enabled").withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Text.translatable("mod.ui.tools").getString());
        for(ToolType tool : ToolType.values()){
            if(tool == ToolType.NONE) continue;
            sb.append("%s".formatted(StringUtils.capitalize(tool.toString().toLowerCase())));
            if(!(tool.ordinal() == ToolType.values().length - 1)){
                // Not last tool, append \n
                sb.append("\n");
            }
        }

        PlayerUtils.sendPlayerMessageChat(
                Text.literal(sb.toString())
        );
        return 1;
    }

    private static int runChoice(CommandContext<FabricClientCommandSource> context){
        String choice = context.getArgument("tool-name", String.class).toLowerCase();
        String itemName = context.getSource().getPlayer().getMainHandStack().getItem().getName().getString();

        if(choice.equalsIgnoreCase("unbind") || choice.equalsIgnoreCase("none")){
            PlayerVariableManager.unbindTool(itemName);
            return 1;
        }

        if(context.getSource().getPlayer().getMainHandStack().getItem() instanceof BlockItem
            || itemName.equals("Air")){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("command.tool.cannot_bind", StringUtils.capitalize(choice)).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }


        ToolType tool;
        try{
            tool = ToolType.valueOf(choice.toUpperCase());
        } catch (IllegalArgumentException e){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("command.tool.unknown_tool", choice).withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }
        String toolInfo;
        try{
            toolInfo = context.getArgument("tool-info", String.class);
        } catch (IllegalArgumentException e){
            // user has provided no info argument
            toolInfo = "default";
        }
        PlayerVariableManager.bindTool(itemName, tool, toolInfo);
        return 1;
    }
}
