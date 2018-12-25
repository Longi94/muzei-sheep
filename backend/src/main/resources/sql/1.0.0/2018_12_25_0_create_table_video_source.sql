CREATE TABLE video_source (
  id           VARCHAR(255) NOT NULL,
  name         VARCHAR(255),
  processed    BIT          NOT NULL,
  published_at DATETIME     NOT NULL,
  PRIMARY KEY (id)
)
