package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.StructManager;
import net.runelite.cache.definitions.StructDefinition;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class StructDumper
{
    private static final Logger logger = LoggerFactory.getLogger(StructDumper.class);
    private static final Logger log = LoggerFactory.getLogger(StructDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "struct");
        dumpDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();
            StructManager loader = new StructManager(store);
            loader.load();

            for (Map.Entry<Integer, StructDefinition> struct : loader.getStructs().entrySet())
            {
                StructDefinition def = struct.getValue();

                Files.asCharSink(new File(dumpDir, struct.getKey() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
                ++count;
            }
        }

        logger.info("Dumped {} structs to {}", count, dumpDir);
    }
}