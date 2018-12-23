package in.dragonbra.muzeisheepbackend.util;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
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
        new Thread(() -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            try {
                while ((line = input.readLine()) != null) {
                    logger.info(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
