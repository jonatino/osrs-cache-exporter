package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.HitSplatDefinition;
import net.runelite.cache.definitions.loaders.HitSplatLoader;
import net.runelite.cache.fs.*;
import org.example.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class HitSplatDumper
{
    private static final Logger logger = LoggerFactory.getLogger(HitSplatDumper.class);
    private static final Logger log = LoggerFactory.getLogger(HitSplatDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "heightmap");
        dumpDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.CONFIGS);
            Archive archive = index.getArchive(ConfigType.HITSPLAT.getId());

            HitSplatLoader loader = new HitSplatLoader();

            byte[] archiveData = storage.loadArchive(archive);
            ArchiveFiles files = archive.getFiles(archiveData);

            for (FSFile file : files.getFiles())
            {
                byte[] b = file.getContents();

                HitSplatDefinition def = loader.load(b);

                Files.asCharSink(new File(dumpDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
                ++count;
            }
        }

        log.info("Dumped {} hitsplats to {}", count, dumpDir);
    }
}