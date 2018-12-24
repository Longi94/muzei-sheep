package in.dragonbra.muzeisheepbackend.cmd;

import in.dragonbra.muzeisheepbackend.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author lngtr
 * @since 2018-12-24
 */
public class YoutubeDl {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeDl.class);

    /**
     * Download a youtube video to a webm file. <a href="https://github.com/rg3/youtube-dl">youtube-dl</a> must be
     * installed on the running machine.
     *
     * @param id The ID of the Youtube video
     * @return a {@link File} object pointing to the downloaded video file
     * @throws IOException exception while executing the youtube-dl command
     */
    public static File download(String id, String outputDirectory) throws IOException {
        logger.info(String.format("Downloading youtube video %s...", id));

        String output = outputDirectory + "/" + id + ".webm";

        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(String.format("youtube-dl -f webm -o %s https://www.youtube.com/watch?v=%s", output, id));

        try {
            ProcessUtil.watch(p, logger);
            p.waitFor();

            logger.info("Finished downloading " + id);

            return new File(output);
        } catch (InterruptedException e) {
            logger.error("Failed to download " + id, e);
        }

        return null;
    }
}
