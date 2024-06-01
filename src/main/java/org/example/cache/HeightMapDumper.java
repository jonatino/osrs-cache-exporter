package org.example.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.constants.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HeightMapDumper
{
    private static final Logger logger = LoggerFactory.getLogger(HeightMapDumper.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void dump() throws IOException
    {
        if (true) {
            logger.error("Height map dumper is disabled");
            return;
        }

        File outDir = new File(Constants.DUMP_DIR, "heightmap");
        outDir.mkdirs();

        int count = 0;

        try (Store store = new Store(Constants.STORAGE))
        {
            store.load();

            net.runelite.cache.HeightMapDumper dumper = new net.runelite.cache.HeightMapDumper(store);
            dumper.load(null);

            BufferedImage image = dumper.drawHeightMap(0);

            File imageFile = new File(outDir, "heightmap-0.png");

            ImageIO.write(image, "png", imageFile);
            logger.info("Wrote image {}", imageFile);
        }
    }
}