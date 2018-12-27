package in.dragonbra.muzeisheepbackend.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lngtr
 * @since 2018-12-25
 */
@Entity
@Table(name = "video_source")
public class VideoSource {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @Column(name = "title")
    private String title;

    @Column(name = "published_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }
}
