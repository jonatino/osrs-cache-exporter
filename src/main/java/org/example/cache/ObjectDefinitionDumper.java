package org.example.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;

public class ObjectDefinitionDumper
{
    private static final Logger logger = LoggerFactory.getLogger(ObjectDefinitionDumper.class);
    private static final Logger log = LoggerFactory.getLogger(ObjectDefinitionDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "object");
        dumpDir.mkdirs();
        File javaDir = new File(dumpDir, "java");
        javaDir.mkdirs();

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            ObjectManager dumper = new ObjectManager(
                    store
            );
            dumper.load();
            dumper.dump(dumpDir);
            dumper.java(javaDir);
        }

        logger.info("Dumped to {}, java {}", dumpDir, javaDir);
    }
}