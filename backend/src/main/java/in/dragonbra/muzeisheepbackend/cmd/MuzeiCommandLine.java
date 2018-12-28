package in.dragonbra.muzeisheepbackend.cmd;

import in.dragonbra.muzeisheepbackend.yt.YoutubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author lngtr
 * @since 2018-12-28
 */
@Component
public class MuzeiCommandLine implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MuzeiCommandLine.class);

    private final ApplicationContext ctx;

    private final YoutubeService youtubeService;

    @Autowired
    public MuzeiCommandLine(ApplicationContext ctx, YoutubeService youtubeService) {
        this.ctx = ctx;
        this.youtubeService = youtubeService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            switch (args[0]) {
                case "load_all":
                    youtubeService.getUploadedVideos(true);
                    SpringApplication.exit(ctx);
                    break;
                default:
                    logger.warn("Unknown command " + args[0]);
                    break;
            }
        } else {
            logger.info("No commands supplied, running normally");
        }
    }
}
