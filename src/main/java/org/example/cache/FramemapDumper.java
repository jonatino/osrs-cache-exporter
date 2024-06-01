package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.FramemapDefinition;
import net.runelite.cache.definitions.loaders.FramemapLoader;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FramemapDumper
{
    private static final Logger logger = LoggerFactory.getLogger(FramemapDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File outDir = new File(Constants.DUMP_DIR, "framemap");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.SKELETONS);

            for (Archive archive : index.getArchives())
            {
                byte[] archiveData = storage.loadArchive(archive);
                byte[] contents = archive.decompress(archiveData);

                FramemapLoader loader = new FramemapLoader();
                FramemapDefinition framemap = loader.load(0, contents);

                Files.asCharSink(new File(outDir, archive.getArchiveId() + ".json"), Charset.defaultCharset()).write(gson.toJson(framemap));
                ++count;
            }
        }

        logger.info("Dumped {} framemaps to {}", count, outDir);
    }
}
