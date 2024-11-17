package me.cocopopeater.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.gsonadapters.SimpleBlockPosAdapter;
import me.cocopopeater.regions.ClipboardRegion;
import me.cocopopeater.util.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.PlayerVariableManager;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.reflect.Type;
public class FileManager {

    private static final String MAIN_DIRECTORY = "CWE";
    private static final String SCHEMATIC_DIRECTORY = MAIN_DIRECTORY.concat("Schematics");

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(SimpleBlockPos.class, new SimpleBlockPosAdapter())
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private static boolean ensureFileExists(File file){
        // Ensure the parent directories exist
        if (!file.getParentFile().exists()) {
            boolean created = file.getParentFile().mkdirs();
            if (created) {
                System.out.println("Parent directories created.");
                return true;
            } else {
                System.err.println("Failed to create parent directories.");
                return false;
            }
        }
        return true;
    }

    public static void saveRegion(ClipboardRegion region, String schematicName){
        if(region == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save region: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        File file = new File("%s/%s.json".formatted(SCHEMATIC_DIRECTORY, schematicName));

        if(!ensureFileExists(file)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save region: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        try (Writer writer = new FileWriter(file)){
            gson.toJson(region, writer);
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Region saved as: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        }catch(IOException e){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save region: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
    }

    public static ClipboardRegion loadRegion(String schematicName){
        File file = new File("%s/%s.json".formatted(SCHEMATIC_DIRECTORY, schematicName));

        if(!ensureFileExists(file)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to load region: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return null;
        }

        ClipboardRegion region = null;


        try (Reader reader = new FileReader(file)) {
            Type regType = new TypeToken<ClipboardRegion>() {}.getType();

            region = gson.fromJson(reader, regType);
            PlayerVariableManager.setClipboardRegion(region);
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Successfully loaded region: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        } catch (IOException e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to load region: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
        
        return region;

    }
}
