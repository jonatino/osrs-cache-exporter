package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.WorldMapDefinition;
import net.runelite.cache.definitions.loaders.WorldMapLoader;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class WorldMapDumper
{
    private static final Logger logger = LoggerFactory.getLogger(WorldMapDumper.class);
    private static final Logger log = LoggerFactory.getLogger(WorldMapDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File outDir = new File(Constants.DUMP_DIR, "worldmap");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.WORLDMAP);
            Archive archive = index.findArchiveByName("details");

            byte[] archiveData = storage.loadArchive(archive);
            ArchiveFiles files = archive.getFiles(archiveData);

            for (FSFile file : files.getFiles())
            {
                WorldMapLoader loader = new WorldMapLoader();
                WorldMapDefinition def = loader.load(file.getContents(), file.getFileId());

                Files.asCharSink(new File(outDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
                ++count;
            }
        }

        logger.info("Dumped {} world map data to {}", count, outDir);
    }
}