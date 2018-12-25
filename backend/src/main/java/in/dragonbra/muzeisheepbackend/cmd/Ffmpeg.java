package in.dragonbra.muzeisheepbackend.cmd;

import in.dragonbra.muzeisheepbackend.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author lngtr
 * @since 2018-12-24
 */
public class Ffmpeg {

    private static final Logger logger = LoggerFactory.getLogger(Ffmpeg.class);

    /**
     * Count the number of frames in a file using ffprobe.
     *
     * @param file the video file
     * @return number of frames
     * @throws IOException io exception
     */
    public static int countFrames(File file) throws IOException {
        logger.info(String.format("counting frames for %s...", file.toString()));

        Runtime rt = Runtime.getRuntime();

        Process p = rt.exec(String.format("ffprobe -v error -count_frames -select_streams v:0 -show_entries stream=nb_read_frames -of default=nokey=1:noprint_wrappers=1 %s",
                file.getAbsolutePath()));
        ProcessUtil.watchStream(p.getErrorStream(), logger, Level.ERROR);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String s;
        while ((s = stdInput.readLine()) != null) {
            try {
                int count = Integer.parseInt(s);
                logger.info("counted " + count + " frames");
                return count;
            } catch (NumberFormatException ignored) {
            }
        }

        logger.warn("failed to count frames");
        return -1;
    }

    /**
     * Extract a specific frame from a video file using ffmpeg into a png file.
     *
     * @param file  the video file
     * @param frame the frame to extract
     * @throws IOException io exception
     */
    public static void extractFrame(File file, int frame, File out) throws IOException, InterruptedException {
        logger.info(String.format("extracting frame %d from %s...", frame, file.toString()));
        Runtime rt = Runtime.getRuntime();

        Process p = rt.exec(String.format("ffmpeg -y -i %s -vf \"select=gte(n\\,%d)\" -vframes 1 %s",
                file.getAbsolutePath(), frame, out.getAbsolutePath()));
        ProcessUtil.watchStream(p.getErrorStream(), logger, Level.INFO);

        p.waitFor();

        if (out.exists()) {
            logger.info(String.format("Saved frame %d to %s", frame, out.toString()));
        } else {
            logger.info(String.format("Failed to frame %d to %s", frame, out.toString()));
        }
    }
}
