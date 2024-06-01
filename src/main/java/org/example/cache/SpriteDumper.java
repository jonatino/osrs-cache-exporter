package org.example.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.SpriteManager;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;

public class SpriteDumper
{
    private static final Logger logger = LoggerFactory.getLogger(SpriteDumper.class);
    private static final Logger log = LoggerFactory.getLogger(SpriteDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "sprite");
        dumpDir.mkdirs();

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            SpriteManager dumper = new SpriteManager(
                    store
            );
            dumper.load();
            dumper.export(dumpDir);
        }

        logger.info("Dumped to {}", dumpDir);
    }
}