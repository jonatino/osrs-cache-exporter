package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.OverlayDefinition;
import net.runelite.cache.definitions.loaders.OverlayLoader;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class OverlayDumper
{
    private static final Logger logger = LoggerFactory.getLogger(OverlayDumper.class);
    private static final Logger log = LoggerFactory.getLogger(OverlayDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File outDir = new File(Constants.DUMP_DIR, "overlay");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.CONFIGS);
            Archive archive = index.getArchive(ConfigType.OVERLAY.getId());

            byte[] archiveData = storage.loadArchive(archive);
            ArchiveFiles files = archive.getFiles(archiveData);

            for (FSFile file : files.getFiles())
            {
                OverlayLoader loader = new OverlayLoader();
                OverlayDefinition overlay = loader.load(file.getFileId(), file.getContents());

                Files.asCharSink(new File(outDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(overlay));
                ++count;
            }
        }

        logger.info("Dumped {} overlays to {}", count, outDir);
    }
}