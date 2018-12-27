package in.dragonbra.muzeisheepbackend.yt;

import in.dragonbra.muzeisheepbackend.cmd.Ffmpeg;
import in.dragonbra.muzeisheepbackend.cmd.YoutubeDl;
import in.dragonbra.muzeisheepbackend.entity.VideoSource;
import in.dragonbra.muzeisheepbackend.image.ImageTool;
import in.dragonbra.muzeisheepbackend.repository.VideoSourceRepository;
import in.dragonbra.muzeisheepbackend.retrofit.YoutubeInterface;
import in.dragonbra.muzeisheepbackend.retrofit.model.YoutubeResponse;
import in.dragonbra.muzeisheepbackend.retrofit.model.youtube.PlaylistItem;
import in.dragonbra.muzeisheepbackend.util.DateUtil;
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
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Date;

/**
 * @author lngtr
 * @since 2018-12-25
 */
@Service
public class YoutubeService {

    private static final Logger logger = LogManager.getLogger(YoutubeService.class);

    private static final double EPSILON = 0.05;

    @Value("${youtube.channel.uploads-id}")
    private String playlistId;

    @Value("${output-folder}")
    private String outputFolder;

    @Value("${temporary-folder}")
    private String tempFolder;

    private final YoutubeInterface youtubeInterface;

    private final VideoSourceRepository videoSourceRepository;

    @Autowired
    public YoutubeService(YoutubeInterface youtubeInterface, VideoSourceRepository videoSourceRepository) {
        this.youtubeInterface = youtubeInterface;
        this.videoSourceRepository = videoSourceRepository;
    }

    public void getUploadedVideos(boolean all) throws IOException, InterruptedException {
        logger.info("Starting video processing...");

        YoutubeResponse youtubeResponse = getPlaylistItems(null);

        if (youtubeResponse == null) {
            return;
        }

        VideoSource latestSource = videoSourceRepository.findFirstByOrderByPublishedAtDesc();
        Date latest = latestSource == null ? new Date(0L) : latestSource.getPublishedAt();

        logger.debug("Latest video source publish date: " + latest.toString());

        boolean running;

        do {
            String nextPageToken = youtubeResponse.getNextPageToken();

            running = handleYoutubeResponse(youtubeResponse, latest, all) && nextPageToken != null;

            if (running) {
                youtubeResponse = getPlaylistItems(nextPageToken);

                if (youtubeResponse == null) {
                    return;
                }
            }
        } while (running);

        logger.info("Finished processing videos");
    }

    private YoutubeResponse getPlaylistItems(String pageToken) throws IOException {
        logger.info(String.format("Loading videos with page token %s", pageToken));

        Call<YoutubeResponse> call =
                youtubeInterface.listPlaylistItems(playlistId, "snippet", 50, pageToken);

        Response<YoutubeResponse> response = call.execute();

        if (!response.isSuccessful()) {
            logger.error("Failed to download video playlist");
            logger.error(response.code());
            logger.error(response.message());
            return null;
        }

        YoutubeResponse youtubeResponse = response.body();

        if (youtubeResponse == null) {
            logger.error("Response body null");
            return null;
        }

        return youtubeResponse;
    }

    private boolean handleYoutubeResponse(YoutubeResponse youtubeResponse, Date latest, boolean all) throws IOException, InterruptedException {
        logger.info("Processing youtube playlistItems response");

        for (PlaylistItem playlistItem : youtubeResponse.getItems()) {
            String id = playlistItem.getSnippet().getResourceId().getVideoId();

            VideoSource videoSource = videoSourceRepository.findById(id).orElse(null);

            if (videoSource == null) {
                videoSource = new VideoSource();
                videoSource.setId(id);
            }

            if (videoSource.isProcessed()) {
                logger.info(String.format("Skipping already processed %s...", id));
                continue;
            }

            Date itemDate;

            try {
                itemDate = DateUtil.ISO_8601.parse(playlistItem.getSnippet().getPublishedAt());
            } catch (ParseException e) {
                logger.error("Failed to parse date", e);

                if (all) {
                    continue;
                } else {
                    return false;
                }
            }

            if (latest.after(itemDate) && !all) {
                logger.info("Reached a video we've already seen");
                return false;
            }

            videoSource.setTitle(playlistItem.getSnippet().getTitle());
            videoSource.setPublishedAt(itemDate);

            processVideo(id, videoSource);

            videoSourceRepository.save(videoSource);
        }

        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void processVideo(String id, VideoSource videoSource) throws IOException, InterruptedException {
        logger.info(String.format("Processing video %s...", id));

        // create output dir
        new File(outputFolder).mkdirs();

        File temp = new File(tempFolder, id);
        temp.mkdirs();

        File videoFile = YoutubeDl.download(id, temp);

        if (!videoFile.exists()) {
            logger.warn("Failed to download video " + id);
            videoSource.setProcessed(false);
        }

        int frameCount = Ffmpeg.countFrames(videoFile);

        if (frameCount < 1) {
            logger.warn("Failed to count frames for " + id);
            videoSource.setProcessed(false);
        }

        File[] frames = Ffmpeg.extractFrames(videoFile, frameCount, temp);

        if (!frames[0].exists() || !frames[1].exists() || !frames[2].exists()) {
            logger.error("Failed to extract frames from " + id);
            videoSource.setProcessed(false);
        }

        logger.info("Calculating similarity scores for screenshots...");
        double score1 = ImageTool.similarity(frames[0], frames[1]);
        double score2 = ImageTool.similarity(frames[2], frames[1]);
        double score3 = ImageTool.similarity(frames[0], frames[2]);

        logger.info(String.format("Similarity scores [%s,%s,%s]", score1, score2, score3));

        if (score1 < EPSILON && score2 < EPSILON && score3 < EPSILON) {
            // safe to assume that the video is a still image, keep a screenshot for the wallpaper
            logger.info("Saving screenshot");
            File resultFile = new File(outputFolder, id + ".png");
            Files.copy(frames[1].toPath(), resultFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            videoSource.setHasImage(true);
        } else {
            videoSource.setHasImage(false);
            logger.info("Screenshots are not similar");
        }

        logger.info("Cleaning up...");
        for (File frame : frames) {
            frame.delete();
        }
        videoFile.delete();
        temp.delete();
    }
}
