package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.TrackDefinition;
import net.runelite.cache.definitions.loaders.TrackLoader;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.Djb2Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;

public class MusicDumper
{
    private static final Logger logger = LoggerFactory.getLogger(MusicDumper.class);
    private static final Logger log = LoggerFactory.getLogger(MusicDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Djb2Manager djb2 = new Djb2Manager();

    public static void dump() throws IOException
    {
        File dumpDir1 = new File(Constants.DUMP_DIR, "music_tracks");
        dumpDir1.mkdirs();
        File dumpDir2 = new File(Constants.DUMP_DIR, "music_jingles");
        dumpDir2.mkdirs();

        int idx1 = 0, idx2 = 0;

        djb2.load();

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index index = store.getIndex(IndexType.MUSIC_TRACKS);
            Index index2 = store.getIndex(IndexType.MUSIC_JINGLES);

            for (Archive archive : index.getArchives())
            {
                dumpTrackArchive(dumpDir1, storage, archive);
                ++idx1;
            }

            for (Archive archive : index2.getArchives())
            {
                dumpTrackArchive(dumpDir2, storage, archive);
                ++idx2;
            }
        }

        logger.info("Dumped {} sound tracks ({} idx1, {} idx2) to {} and {}", idx1 + idx2, idx1, idx2, dumpDir1, dumpDir2);
    }

    private static void dumpTrackArchive(File dumpDir, Storage storage, Archive archive) throws IOException
    {
        byte[] contents = archive.decompress(storage.loadArchive(archive));

        if (contents == null)
        {
            return;
        }

        TrackLoader loader = new TrackLoader();
        TrackDefinition def = loader.load(contents);

        String name;
        if (archive.getNameHash() != 0)
        {
            name = djb2.getName(archive.getNameHash());
            if (name == null)
            {
                name = "name-" + archive.getNameHash();
            }
        }
        else
        {
            name = "archive-" + archive.getArchiveId();
        }

        File dest = new File(dumpDir, name + ".midi");
        assert !dest.exists();

        Files.write(def.midi, dest);
    }
}