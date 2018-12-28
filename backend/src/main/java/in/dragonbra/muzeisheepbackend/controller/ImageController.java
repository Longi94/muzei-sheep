package in.dragonbra.muzeisheepbackend.controller;

import in.dragonbra.muzeisheepbackend.controller.exception.BadRequestException;
import in.dragonbra.muzeisheepbackend.controller.exception.NotFoundException;
import in.dragonbra.muzeisheepbackend.entity.VideoSource;
import in.dragonbra.muzeisheepbackend.repository.VideoSourceRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lngtr
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/api")
public class ImageController {

    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile("^[^\"&?/\\s.]{11}$");

    @Value("${output-folder}")
    private String outputFolder;

    private final VideoSourceRepository videoSourceRepository;

    @Autowired
    public ImageController(VideoSourceRepository videoSourceRepository) {
        this.videoSourceRepository = videoSourceRepository;
    }

    @GetMapping(value = "/images")
    public List<VideoSource> getImages() {
        return this.videoSourceRepository.findAll(new Sort(Sort.Direction.DESC, "publishedAt"));
    }

    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable(value = "id", required = true) String id) {
        Matcher idMatcher = YOUTUBE_ID_PATTERN.matcher(id);

        if (!idMatcher.matches()) {
            throw new BadRequestException();
        }

        try {
            File file = new File(outputFolder, id + ".png");
            InputStream is = new FileInputStream(file);
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new NotFoundException();
        }
    }
}
