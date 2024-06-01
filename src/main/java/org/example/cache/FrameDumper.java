package org.example.cache;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.FrameDefinition;
import net.runelite.cache.definitions.FramemapDefinition;
import net.runelite.cache.definitions.loaders.FrameLoader;
import net.runelite.cache.definitions.loaders.FramemapLoader;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FrameDumper
{
    private static final Logger logger = LoggerFactory.getLogger(FrameDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        if (true) {
            logger.error("This shit is broken, skipping");
            return;
        }

        File outDir = new File(Constants.DUMP_DIR, "frame");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            Storage storage = store.getStorage();
            Index frameIndex = store.getIndex(IndexType.ANIMATIONS);
            Index framemapIndex = store.getIndex(IndexType.SKELETONS);

            for (Archive archive : frameIndex.getArchives())
            {
                List<FrameDefinition> frames = new ArrayList<>();

                byte[] archiveData = storage.loadArchive(archive);

                ArchiveFiles archiveFiles = archive.getFiles(archiveData);
                for (FSFile archiveFile : archiveFiles.getFiles())
                {
                    byte[] contents = archiveFile.getContents();

                    int framemapArchiveId = (contents[0] & 0xff) << 8 | contents[1] & 0xff;

                    Archive framemapArchive = framemapIndex.getArchive(framemapArchiveId);
                    archiveData = storage.loadArchive(framemapArchive);
                    byte[] framemapContents = framemapArchive.decompress(archiveData);

                    FramemapLoader fmloader = new FramemapLoader();
                    FramemapDefinition framemap = fmloader.load(framemapArchive.getArchiveId(), framemapContents);

                    FrameLoader frameLoader = new FrameLoader();
                    FrameDefinition frame = frameLoader.load(framemap, archiveFile.getFileId(), contents);

                    frames.add(frame);
                }

                Files.asCharSink(new File(outDir, archive.getArchiveId() + ".json"), Charset.defaultCharset()).write(gson.toJson(frames));
                ++count;
            }
        }

        logger.info("Dumped {} frames to {}", count, outDir);
    }
}
