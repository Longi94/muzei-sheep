CREATE TABLE video_source (
  id           VARCHAR(255) NOT NULL,
  title        VARCHAR(255),
  processed    BIT          NOT NULL,
  published_at DATETIME,
  PRIMARY KEY (id)
)
