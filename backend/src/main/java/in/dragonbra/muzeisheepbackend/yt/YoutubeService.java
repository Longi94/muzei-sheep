package in.dragonbra.muzeisheepbackend.yt;

import in.dragonbra.muzeisheepbackend.cmd.Ffmpeg;
import in.dragonbra.muzeisheepbackend.cmd.YoutubeDl;
import in.dragonbra.muzeisheepbackend.image.ImageTool;
import in.dragonbra.muzeisheepbackend.retrofit.YoutubeInterface;
import in.dragonbra.muzeisheepbackend.retrofit.model.YoutubeResponse;
import in.dragonbra.muzeisheepbackend.retrofit.model.youtube.PlaylistItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author lngtr
 * @since 2018-12-25
 */
@Service
public class YoutubeService {

    private static final Logger logger = LogManager.getLogger(YoutubeService.class);

    private static final double EPSILON = 0.01;

    @Value("${youtube.channel.uploads-id}")
    private String playlistId;

    @Value("${output-folder}")
    private String outputFolder;

    @Value("${temporary-folder}")
    private String tempFolder;

    private final YoutubeInterface youtubeInterface;

    @Autowired
    public YoutubeService(YoutubeInterface youtubeInterface) {
        this.youtubeInterface = youtubeInterface;
    }

    public void getUploadedVideos() throws IOException, InterruptedException {
        Call<YoutubeResponse> call =
                youtubeInterface.listPlaylistItems(playlistId, "snippet", 50, null);

        Response<YoutubeResponse> response = call.execute();

        if (!response.isSuccessful()) {
            logger.error("Failed to download video playlist");
            logger.error(response.code());
            logger.error(response.message());
            return;
        }

        YoutubeResponse youtubeResponse = response.body();

        for (PlaylistItem playlistItem : youtubeResponse.getItems()) {
            String id = playlistItem.getSnippet().getResourceId().getVideoId();
            processVideo(id);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File processVideo(String id) throws IOException, InterruptedException {
        logger.info(String.format("Processing video %s...", id));

        // create output dir
        new File(outputFolder).mkdirs();

        File temp = new File(tempFolder, id);
        temp.mkdirs();

        File videoFile = YoutubeDl.download(id, temp);

        if (!videoFile.exists()) {
            logger.warn("Failed to download video " + id);
            return null;
        }

        int frames = Ffmpeg.countFrames(videoFile);

        if (frames < 1) {
            logger.warn("Failed to count frames for " + id);
            return null;
        }

        File ss1 = new File(temp, "ss1.png");
        Ffmpeg.extractFrame(videoFile, frames / 4, ss1);

        File ss2 = new File(temp, "ss2.png");
        Ffmpeg.extractFrame(videoFile, frames / 2, ss2);

        File ss3 = new File(temp, "ss3.png");
        Ffmpeg.extractFrame(videoFile, frames / 4 * 3, ss3);

        if (!ss1.exists() || !ss2.exists() || !ss3.exists()) {
            logger.error("Failed to extract frames from " + id);
            return null;
        }

        logger.info("Calculating similarity scores for screenshots...");
        double score1 = ImageTool.similarity(ss1, ss2);
        double score2 = ImageTool.similarity(ss1, ss3);
        double score3 = ImageTool.similarity(ss3, ss2);

        File result = null;

        if (score1 < EPSILON && score2 < EPSILON && score3 < EPSILON) {
            // safe to assume that the video is a still image, keep a screenshot for the wallpaper
            logger.info("Saving screenshot");
            result = new File(outputFolder, id + ".png");
            Files.copy(ss2.toPath(), result.toPath());
        } else {
            logger.info("Screenshots are not similar");
        }

        logger.info("Cleaning up...");
        videoFile.delete();
        ss1.delete();
        ss2.delete();
        ss3.delete();
        temp.delete();

        return result;
    }
}
