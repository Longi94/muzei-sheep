package in.dragonbra.muzeisheepbackend.repository;

import in.dragonbra.muzeisheepbackend.entity.VideoSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author lngtr
 * @since 2018-12-25
 */
public interface VideoSourceRepository extends JpaRepository<VideoSource, String> {
    VideoSource findFirstByOrderByPublishedAtDesc();

    List<VideoSource> findAllByProcessedIsFalse();
}
