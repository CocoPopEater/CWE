package me.cocopopeater;

import me.cocopopeater.commands.*;
import me.cocopopeater.config.FileManager;
import me.cocopopeater.events.ChatSuppressor;
import me.cocopopeater.events.LeftClickBlockEvent;
import me.cocopopeater.events.RightClickBlockEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class CWEClient implements ClientModInitializer {

    private void registerCommands(){
        ClientCommandRegistrationCallback.EVENT.register(ConfigCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(EnableModCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(GiveWandCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(CheckRegionCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(ExpandRegionCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(SetRegionCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(ReplaceRegionCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(RegionCopyCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(RegionPasteCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(SchematicCommand::register);


        ClientCommandRegistrationCallback.EVENT.register(TestCommand::register);


    }

    private void registerEvents(){
        LeftClickBlockEvent.register();
        RightClickBlockEvent.register();
        ChatSuppressor.register();

    }

    @Override
    public void onInitializeClient() {
        registerCommands();
        registerEvents();
    }
}
