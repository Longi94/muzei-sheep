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
     * Extract 3 frames from the video roughly at the quarter break points.
     *
     * @param file       the video file
     * @param frameCount number of frames in the video
     * @param out        the output folder
     * @return File objects pointing to the frames
     * @throws IOException          io exception
     * @throws InterruptedException waitFor interrupted
     */
    public static File[] extractFrames(File file, int frameCount, File out) throws IOException, InterruptedException {
        logger.info(String.format("extracting frames from %s...", file.toString()));
        Runtime rt = Runtime.getRuntime();

        Process p = rt.exec(String.format("ffmpeg -y -i %s -vf select=\"not(mod(n\\,%d)), select=gte(n\\,1)\",setpts=N/TB -r 1 -vframes 3 %s/frame%%03d.png",
                file.getAbsolutePath(), frameCount / 4, out.getAbsolutePath()));
        ProcessUtil.watchStream(p.getErrorStream(), logger, Level.DEBUG);

        p.waitFor();

        File frame1 = new File(out, "frame001.png");
        File frame2 = new File(out, "frame002.png");
        File frame3 = new File(out, "frame003.png");

        if (frame1.exists() && frame2.exists() && frame3.exists()) {
            logger.info(String.format("Saved frames to %s", out.toString()));
        } else {
            logger.info(String.format("Failed to save frames to %s", out.toString()));
        }

        return new File[]{frame1, frame2, frame3};
    }
}
