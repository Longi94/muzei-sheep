package in.dragonbra.muzeisheepbackend.scheduled;

import in.dragonbra.muzeisheepbackend.yt.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lngtr
 * @since 2018-12-27
 */
@Component
public class ScheduledTasks {

    private final YoutubeService youtubeService;

    @Autowired
    public ScheduledTasks(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @Scheduled(cron = "${cron.get-videos}")
    public void getYoutubeVideos() throws IOException, InterruptedException {
        youtubeService.getUploadedVideos(false);
        youtubeService.retryUnprocessed();
    }

}
