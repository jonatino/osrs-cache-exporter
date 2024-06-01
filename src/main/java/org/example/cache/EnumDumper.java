package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.EnumDefinition;
import net.runelite.cache.definitions.loaders.EnumLoader;
import net.runelite.cache.fs.*;
import org.example.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class EnumDumper
{
    private static final Logger logger = LoggerFactory.getLogger(EnumDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "enum");
        dumpDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.CONFIGS);
            Archive archive = index.getArchive(ConfigType.ENUM.getId());

            byte[] archiveData = storage.loadArchive(archive);
            ArchiveFiles files = archive.getFiles(archiveData);

            EnumLoader loader = new EnumLoader();

            for (FSFile file : files.getFiles())
            {
                byte[] b = file.getContents();

                EnumDefinition def = loader.load(file.getFileId(), b);

                if (def != null)
                {
                    Files.asCharSink(new File(dumpDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
                    ++count;
                }
            }
        }

        logger.info("Dumped {} enums to {}", count, dumpDir);
    }
}
