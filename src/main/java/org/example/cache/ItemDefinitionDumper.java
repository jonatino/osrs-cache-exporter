package org.example.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.ItemManager;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;

public class ItemDefinitionDumper
{
    private static final Logger logger = LoggerFactory.getLogger(ItemDefinitionDumper.class);
    private static final Logger log = LoggerFactory.getLogger(ItemDefinitionDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "item");
        dumpDir.mkdirs();
        File javaDir = new File(dumpDir, "java");
        javaDir.mkdirs();

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            ItemManager dumper = new ItemManager(
                    store
            );
            dumper.load();
            dumper.export(dumpDir);
            dumper.java(javaDir);

            logger.info("Dumped to {}, java {}", dumpDir, javaDir);
        }
    }
}