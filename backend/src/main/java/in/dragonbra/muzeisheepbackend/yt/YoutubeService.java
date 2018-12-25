package in.dragonbra.muzeisheepbackend.yt;

import in.dragonbra.muzeisheepbackend.cmd.Ffmpeg;
import in.dragonbra.muzeisheepbackend.cmd.YoutubeDl;
import in.dragonbra.muzeisheepbackend.image.ImageTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author lngtr
 * @since 2018-12-25
 */
@Service
public class YoutubeService {

    private static final Logger logger = LogManager.getLogger(YoutubeService.class);

    private static final double EPSILON = 0.01;

    @Value("${temporary-folder}")
    private String tempFolder;

    public void processVideo(String id) throws IOException, InterruptedException {
        logger.info(String.format("Processing video %s...", id));

        File temp = new File(tempFolder, id);
        temp.mkdirs();

        File videoFile = YoutubeDl.download(id, temp);

        if (!videoFile.exists()) {
            logger.warn("Failed to download video " + id);
            return;
        }

        int frames = Ffmpeg.countFrames(videoFile);

        if (frames < 1) {
            logger.warn("Failed to count frames for " + id);
            return;
        }

        File ss1 = new File(temp, "ss1.png");
        Ffmpeg.extractFrame(videoFile, frames / 4, ss1);

        File ss2 = new File(temp, "ss2.png");
        Ffmpeg.extractFrame(videoFile, frames / 2, ss2);

        File ss3 = new File(temp, "ss3.png");
        Ffmpeg.extractFrame(videoFile, frames / 4 * 3, ss3);

        if (!ss1.exists() || !ss2.exists() || !ss3.exists()) {
            logger.error("Failed to extract frames from " + id);
            return;
        }

        logger.info("Calculating similarity scores for screenshots...");
        double score1 = ImageTool.similarity(ss1, ss2);
        double score2 = ImageTool.similarity(ss1, ss3);
        double score3 = ImageTool.similarity(ss3, ss2);

        if (!(score1 < EPSILON) || !(score2 < EPSILON) || !(score3 < EPSILON)) {
            logger.info("Screenshots are not similar, deleting all of them");
            ss2.delete();
        }  // else safe to assume that the video is a still image, keep a screenshot for the wallpaper

        logger.info("Cleaning up...");
        videoFile.delete();
        ss1.delete();
        ss3.delete();
    }
}
