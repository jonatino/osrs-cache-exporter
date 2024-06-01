package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.AreaManager;
import net.runelite.cache.definitions.AreaDefinition;
import net.runelite.cache.fs.Store;
import org.example.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class AreaExporter
{
    private static final Logger logger = LoggerFactory.getLogger(AreaExporter.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File outDir = new File(Constants.DUMP_DIR, "area");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            AreaManager areaManager = new AreaManager(store);
            areaManager.load();

            for (AreaDefinition area : areaManager.getAreas())
            {
                Files.asCharSink(new File(outDir, area.id + ".json"), Charset.defaultCharset()).write(gson.toJson(area));
                ++count;
            }
        }

        logger.info("Dumped {} areas to {}", count, outDir);
    }
}
