package me.cocopopeater.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.blocks.TestRegion;
import me.cocopopeater.config.ConfigHandler;
import me.cocopopeater.config.FileManager;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.tasks.TimedCommandRunner;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TestCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("/test")
                .executes(TestCommand::run)
                .then(literal("print").executes(TestCommand::runPrint)));
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {

        CompletableFuture.supplyAsync(() -> {
            return new TestRegion(
                    PlayerVariableManager.getPos1(),
                    PlayerVariableManager.getPos2(),
                    context.getSource().getWorld(),
                    SimpleBlockPos.fromBlockPos(context.getSource().getClient().player.getBlockPos())
            );
        }).thenAccept(testRegion -> {
            FileManager.saveTestRegion(testRegion, "Test_Region", false);
        });

        PlayerUtils.sendPlayerMessageChat(
                Text.literal("Placeholder")
        );
        return 1;
    }

    private static int runPrint(CommandContext<FabricClientCommandSource> context){

        CompletableFuture.runAsync(() -> {
            TestRegion region = FileManager.loadTestRegion("Test_Region", false);

            TimedCommandRunner runner = new TimedCommandRunner();
            for(String command : region.generateFillCommands(SimpleBlockPos.fromBlockPos(context.getSource().getClient().player.getBlockPos()))){
                runner.addTask(() -> PlayerUtils.sendCommandAsPlayer(command));
            }
            runner.start(ConfigHandler.getInstance().getCommandDelay(), TimeUnit.MILLISECONDS);
        });
        return 1;
    }


}
