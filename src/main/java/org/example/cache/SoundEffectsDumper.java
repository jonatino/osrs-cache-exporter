package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.loaders.sound.SoundEffectLoader;
import net.runelite.cache.definitions.sound.SoundEffectDefinition;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class SoundEffectsDumper
{
    private static final Logger logger = LoggerFactory.getLogger(SoundEffectsDumper.class);
    private static final Logger log = LoggerFactory.getLogger(SoundEffectsDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        File dumpDir = new File(Constants.DUMP_DIR, "sound_effects");
        dumpDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.SOUNDEFFECTS);

            for (Archive archive : index.getArchives())
            {
                byte[] contents = archive.decompress(storage.loadArchive(archive));

                SoundEffectLoader soundEffectLoader = new SoundEffectLoader();
                SoundEffectDefinition soundEffect = soundEffectLoader.load(contents);

                Files.asCharSink(new File(dumpDir, archive.getArchiveId() + ".json"), Charset.defaultCharset()).write(gson.toJson(soundEffect));
                ++count;
            }
        }

        logger.info("Dumped {} sound effects to {}", count, dumpDir);
    }
}