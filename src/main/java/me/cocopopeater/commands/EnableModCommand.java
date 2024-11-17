package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.util.GlobalColorRegistry;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class EnableModCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/enable").executes(EnableModCommand::run));
    }

    public static int run(CommandContext<FabricClientCommandSource> context){

        ConfigHandler.getInstance().setEnabled(!ConfigHandler.getInstance().isEnabled());
        boolean enabled = ConfigHandler.getInstance().isEnabled();

        int rgbColor = enabled ? GlobalColorRegistry.getLimeGreen() : GlobalColorRegistry.getBrightRed();

        Text part1 = Text.literal("CWE has been ");
        Text part2 = Text.literal(enabled ? "activated" : "deactivated")
                .setStyle(Style.EMPTY.withColor(rgbColor));

        Text message = part1.copy().append(part2);
        context.getSource().sendFeedback(message);
        return 1;
    }


}
