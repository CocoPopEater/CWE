package me.cocopopeater.events;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ChatSuppressor {

    private static final HashSet<String> messageKeysToHide = new HashSet<>();

    static {
        // https://gist.github.com/sppmacd/82af47c83b225d4ffd33bb0c27b0d932
        messageKeysToHide.add("commands.setblock.failed");
        messageKeysToHide.add("commands.setblock.success");
        messageKeysToHide.add("commands.fill.failed");
        messageKeysToHide.add("commands.fill.success");
    }

    public static void register(){

        ClientReceiveMessageEvents.ALLOW_GAME.register((text, b) -> {
            for(String key : messageKeysToHide){
                if(text.getString().equals(I18n.translate(key))){
                    return false;
                }
                if(text.getString().contains(I18n.translate(key).replaceAll("%s", "").replaceAll(",", "").trim())){
                    return false;
                }
            }
            return true;
        });
    }
}
