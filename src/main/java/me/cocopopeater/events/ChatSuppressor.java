package me.cocopopeater.events;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatSuppressor {
    private static final ArrayList<String> messagesToHide = new ArrayList<>(Arrays.asList(
            "Could not set the block",
            "No blocks were filled"
    ));

    public static void register(){

        ClientReceiveMessageEvents.ALLOW_GAME.register((text, b) -> {
            for(String s : messagesToHide){
                if(text.getString().contains(s)){
                    return false;
                }
            }
            return true;
        });
    }
}
