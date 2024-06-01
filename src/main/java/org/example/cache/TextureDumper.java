package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.TextureManager;
import net.runelite.cache.definitions.TextureDefinition;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class TextureDumper
{
    private static final Logger logger = LoggerFactory.getLogger(TextureDumper.class);
    private static final Logger log = LoggerFactory.getLogger(TextureDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File outDir = new File(Constants.DUMP_DIR, "texture");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            TextureManager tm = new TextureManager(store);
            tm.load();

            for (TextureDefinition texture : tm.getTextures())
            {
                Files.asCharSink(new File(outDir, texture.getId() + ".json"), Charset.defaultCharset()).write(gson.toJson(texture));
                ++count;
            }
        }

        logger.info("Dumped {} textures to {}", count, outDir);
    }
}