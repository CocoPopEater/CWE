package me.cocopopeater.gsonadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.cocopopeater.blocks.SimpleBlockPos;

import java.io.IOException;

public class SimpleBlockPosAdapter extends TypeAdapter<SimpleBlockPos> {
    @Override
    public void write(JsonWriter out, SimpleBlockPos pos) throws IOException {
        out.value(String.format("{x:%d,y:%d,z:%d}", pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public SimpleBlockPos read(JsonReader in) throws IOException {
        String value = in.nextString();
        String[] parts = value.replace("{", "").replace("}", "").split(",");
        int x = Integer.parseInt(parts[0].split(":")[1]);
        int y = Integer.parseInt(parts[1].split(":")[1]);
        int z = Integer.parseInt(parts[2].split(":")[1]);
        return new SimpleBlockPos(x, y, z);
    }
}
