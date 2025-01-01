package me.cocopopeater.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.cocopopeater.blocks.SimpleBlockPos;
import me.cocopopeater.regions.SchematicRegion;
import me.cocopopeater.util.gsonadapters.SimpleBlockPosAdapter;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import me.cocopopeater.util.PlayerUtils;
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
                    Text.translatable("files.error.schematic_directory_missing")
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
                    Text.translatable("files.error.insufficient_permission")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
        return fileNames;
    }

    public static void saveSchematic(SchematicRegion region, String schematicName){
        if(region == null){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.invalid_region")
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        File file = new File("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName));

        if(!ensureFileExists(file)){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.file_create_failed", schematicName)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            String data = gson.toJson(region);
            byte[] compressed = compress(data);
            fileOutputStream.write(compressed);
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.success.region_saved", schematicName)
                            .withColor(GlobalColorRegistry.getLimeGreen())
            );
        }catch(IOException e){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.ioexception", schematicName)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        } catch (Exception e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.exception", schematicName)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        }
    }

    public static SchematicRegion loadSchematic(String schematicName){
        File file = new File("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName));
        if(!file.exists()){
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.cannot_find_file", schematicName, file.getPath())
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
            return null;
        }

        SchematicRegion region = null;
        byte[] data;
        try(FileInputStream fileInputStream = new FileInputStream("%s/%s.cschem".formatted(SCHEMATIC_DIRECTORY, schematicName))){
            data = fileInputStream.readAllBytes();

            Type regType = new TypeToken<SchematicRegion>() {}.getType();
            String decomp = decompress(data);
            region = gson.fromJson(decomp, regType);


        } catch (IOException e) {
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.ioexception", schematicName)
                            .withColor(GlobalColorRegistry.getBrightRed())
            );
        } catch (Exception e) {
            e.printStackTrace();
            PlayerUtils.sendPlayerMessageChat(
                    Text.translatable("files.error.exception", schematicName)
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
            return byteArrayOutputStream.toString();
        }
    }
}
