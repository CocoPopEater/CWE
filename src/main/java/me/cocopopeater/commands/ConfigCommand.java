package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.*;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCommand {
    private static final SuggestionProvider<FabricClientCommandSource> OPERATION_SUGGESTIONS = (context, builder) -> {
        builder.suggest("get");
        builder.suggest("set");
        return builder.buildFuture();
    };

    private static final SuggestionProvider<FabricClientCommandSource> OPTION_SUGGESTIONS = (context, builder) -> {
        builder.suggest("enabled");
        builder.suggest("parallel-processing");
        builder.suggest("command-delay");
        return builder.buildFuture();
    };
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/config")
                .then(argument("operation", StringArgumentType.string()).suggests(OPERATION_SUGGESTIONS)
                        .then(argument("configOption", StringArgumentType.string()).suggests(OPTION_SUGGESTIONS)
                                .executes(ConfigCommand::run)
                                .then(argument("value", StringArgumentType.string())
                                    .executes(ConfigCommand::run)))));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){
        String get_or_set = context.getArgument("operation", String.class);
        String config_option = context.getArgument("configOption", String.class);

        if(get_or_set.equalsIgnoreCase("get")){
            handleConfigGet(config_option);
        }else if(get_or_set.equalsIgnoreCase("set")){
            String value = context.getArgument("value", String.class);
            if(value == null || value.isEmpty()){
                PlayerUtils.sendPlayerMessageChat(
                        Text.literal("Invalid 3rd argument")
                                .withColor(GlobalColorRegistry.getBrightRed())
                );
            }
            handleConfigSet(config_option, value);
        }else{
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("command.error.invalid_argument", get_or_set)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return 0;
        }

        return 1;
    }

    private static void handleConfigGet(String configOption){
        switch(configOption.toLowerCase()){
            case "enabled" -> {
                boolean enabled = ConfigHandler.getInstance().isEnabled();
                int rgbColor = enabled ? GlobalColorRegistry.getLimeGreen() : GlobalColorRegistry.getBrightRed();


                Text message = Text.translatable("command.config.mod_status",
                        Text.translatable(enabled ? "mod.generic.activated" : "mod.generic.deactivated")
                                .setStyle(Style.EMPTY).withColor(rgbColor));

                PlayerUtils.sendPlayerMessageChat(
                        message
                );
            }
            case "parallel-processing" -> {
                boolean isParallelProcessing = ConfigHandler.getInstance().isParallelProcessing();
                int rgbColor = isParallelProcessing ? GlobalColorRegistry.getLimeGreen() : GlobalColorRegistry.getBrightRed();

                Text message = Text.translatable("command.config.parallel_processing_status",
                        Text.translatable(isParallelProcessing ? "mod.generic.activated" : "mod.generic.deactivated")
                                .setStyle(Style.EMPTY).withColor(rgbColor));


                PlayerUtils.sendPlayerMessageChat(
                        message
                );
            }
            case "command-delay" -> {
                int delay = ConfigHandler.getInstance().getCommandDelay();

                Text message = Text.translatable("command.config.command_delay",
                        Text.literal("%d ms".formatted(delay)).withColor(GlobalColorRegistry.getLimeGreen()));
                PlayerUtils.sendPlayerMessageChat(
                        message
                );
            }
            default -> PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("command.config.unknown_key", configOption)
            );
        }
    }

    private static void handleConfigSet(String configOption, String value){
        switch(configOption.toLowerCase()){
            case "enabled" -> {
                boolean flag;
                if(value.equalsIgnoreCase("true")) {
                    flag = true;
                }else if (value.equalsIgnoreCase("false")){
                    flag = false;
                }else{
                    PlayerUtils.sendPlayerMessageChat(
                            Text.translatable("mod.generic.invalid_boolean", value).withColor(GlobalColorRegistry.getBrightRed())
                    );
                    return;
                }
                ConfigHandler.getInstance().setEnabled(flag);

                boolean enabled = ConfigHandler.getInstance().isEnabled();
                int rgbColor = enabled ? GlobalColorRegistry.getLimeGreen() : GlobalColorRegistry.getBrightRed();

                Text message = Text.translatable("command.config.mod_status",
                        Text.translatable(enabled ? "mod.generic.activated" : "mod.generic.deactivated")
                                .setStyle(Style.EMPTY).withColor(rgbColor));

                PlayerUtils.sendPlayerMessageChat(
                        message
                );
            }
            case "parallel-processing" -> {
                boolean flag;
                if(value.equalsIgnoreCase("true")) {
                    flag = true;
                }else if (value.equalsIgnoreCase("false")){
                    flag = false;
                }else{
                    PlayerUtils.sendPlayerMessageChat(
                            Text.translatable("mod.generic.invalid_boolean", value).withColor(GlobalColorRegistry.getBrightRed())
                    );
                    return;
                }
                ConfigHandler.getInstance().setParallelProcessing(flag);

                boolean isParallelProcessing = ConfigHandler.getInstance().isParallelProcessing();
                int rgbColor = isParallelProcessing ? GlobalColorRegistry.getLimeGreen() : GlobalColorRegistry.getBrightRed();

                Text message = Text.translatable("command.config.parallel_processing_status",
                        Text.translatable(isParallelProcessing ? "mod.generic.activated" : "mod.generic.deactivated")
                                .setStyle(Style.EMPTY).withColor(rgbColor));

                PlayerUtils.sendPlayerMessageChat(
                        message
                );
            }
            case "command-delay" -> {
                int newDelay;
                try{
                   newDelay = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    PlayerUtils.sendPlayerMessageChat(
                            Text.translatable("mod.generic.invalid_integer", value)
                    );
                    return;
                }
                ConfigHandler.getInstance().setCommandDelay(newDelay);
            }
        }
    }
}
