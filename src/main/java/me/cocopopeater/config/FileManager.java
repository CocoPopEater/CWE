package me.cocopopeater.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.blocks.TestRegion;
import me.cocopopeater.util.gsonadapters.SimpleBlockPosAdapter;
import me.cocopopeater.regions.ClipboardRegion;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.PlayerVariableManager;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class FileManager {

    private static final String MAIN_DIRECTORY = "CWE";
    private static final String SCHEMATIC_DIRECTORY = MAIN_DIRECTORY.concat("/Schematics");

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(SimpleBlockPos.class, new SimpleBlockPosAdapter())
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private static boolean ensureFileExists(File file) {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return file.exists() || file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> getSchematicList(){
        File file = new File(SCHEMATIC_DIRECTORY);
        file.mkdirs();
        if(!ensureFileExists(file)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to load schematic list")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return new ArrayList<>();
        }
        ArrayList<String> fileNames = new ArrayList<>();

        try {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    fileNames.add(f.getName().replace(".cschem", ""));
                }
            }
        } catch (SecurityException e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to retrieve schematics")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }

        return fileNames;
    }

    public static void saveSchematic(ClipboardRegion region, String schematicName){
        if(region == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        File file = new File("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName));

        if(!ensureFileExists(file)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            String data = gson.toJson(region);
            byte[] compressed = compress(data);
            fileOutputStream.write(compressed);
            //gson.toJson(region, writer);
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Region saved as: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        }catch(IOException e){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        } catch (Exception e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
    }

    public static ClipboardRegion loadSchematic(String schematicName){
        File file = new File("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName));
        if(!file.exists()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("FNE: Unable to load schematic: %s from path: %s".formatted(schematicName, file.getPath()))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return null;
        }

        ClipboardRegion region = null;
        byte[] data;
        try(FileInputStream fileInputStream = new FileInputStream("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName))){
            data = fileInputStream.readAllBytes();

            Type regType = new TypeToken<ClipboardRegion>() {}.getType();
            String decomp = decompress(data);
            region = gson.fromJson(decomp, regType);
            PlayerVariableManager.setClipboardRegion(region);

            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Successfully loaded schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        } catch (IOException e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("IOE: Unable to load schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        } catch (Exception e) {
            e.printStackTrace();
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("E: Unable to load schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
        return region;
    }

    public static byte[] compress(String data) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream)) {
            deflaterOutputStream.write(data.getBytes());
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String decompress(byte[] compressedData) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inflaterInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return new String(byteArrayOutputStream.toByteArray());
        }
    }

    public static TestRegion loadTestRegion(String schematicName){
        File file = new File("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName));
        if(!file.exists()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("FNE: Unable to load schematic: %s from path: %s".formatted(schematicName, file.getPath()))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return null;
        }

        TestRegion region = null;
        byte[] data;
        try(FileInputStream fileInputStream = new FileInputStream("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName))){
            data = fileInputStream.readAllBytes();

            Type regType = new TypeToken<TestRegion>() {}.getType();
            String decomp = decompress(data);
            region = gson.fromJson(decomp, regType);

            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Successfully loaded schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        } catch (IOException e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("IOE: Unable to load schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        } catch (Exception e) {
            e.printStackTrace();
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("E: Unable to load schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
        return region;
    }

    public static void saveTestRegion(TestRegion region, String schematicName){
        if(region == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        File file = new File("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName));

        if(!ensureFileExists(file)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            String data = gson.toJson(region);
            byte[] compressed = compress(data);
            fileOutputStream.write(compressed);
            //gson.toJson(region, writer);
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Region saved as: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        }catch(IOException e){
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        } catch (Exception e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.literal("Unable to save schematic: %s".formatted(schematicName))
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
    }


}
