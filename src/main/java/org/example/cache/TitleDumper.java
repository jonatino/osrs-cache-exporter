package org.example.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.IndexType;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TitleDumper
{
    private static final Logger logger = LoggerFactory.getLogger(TitleDumper.class);
    private static final Logger log = LoggerFactory.getLogger(TitleDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File outDir = new File(Constants.DUMP_DIR, "title.jpg");

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.BINARY);
            Archive archive = index.findArchiveByName("title.jpg");
            byte[] contents = archive.decompress(storage.loadArchive(archive));

            Files.write(outDir.toPath(), contents);
        }

        logger.info("Dumped to {}", outDir);
    }
}