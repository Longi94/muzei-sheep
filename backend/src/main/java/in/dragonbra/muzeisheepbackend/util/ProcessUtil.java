package in.dragonbra.muzeisheepbackend.util;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author lngtr
 * @since 2018-12-24
 */
public class ProcessUtil {

    /**
     * Log the output of the process to the specified logger.
     *
     * @param process the {@link Process} object to watch
     */
    public static void watch(final Process process, final Logger logger) {
        watchStream(process.getInputStream(), logger, Level.INFO);
        watchStream(process.getErrorStream(), logger, Level.ERROR);
    }

    public static void watchStream(final InputStream stream, final Logger logger, final Level level) {
        new Thread(() -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(stream));
            String line;
            try {
                switch (level) {
                    case ERROR:
                        while ((line = input.readLine()) != null) {
                            logger.error(line);
                        }
                        break;
                    case WARN:
                        while ((line = input.readLine()) != null) {
                            logger.warn(line);
                        }
                        break;
                    case INFO:
                        while ((line = input.readLine()) != null) {
                            logger.info(line);
                        }
                        break;
                    case DEBUG:
                        while ((line = input.readLine()) != null) {
                            logger.debug(line);
                        }
                        break;
                    case TRACE:
                        while ((line = input.readLine()) != null) {
                            logger.trace(line);
                        }
                        break;
                }
            } catch (IOException e) {
                logger.error("Failed to read stream", e);
            }
        }).start();
    }
}
