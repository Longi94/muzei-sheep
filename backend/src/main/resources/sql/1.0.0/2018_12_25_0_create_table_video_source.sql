CREATE TABLE video_source (
  id           VARCHAR(255) NOT NULL,
  title        VARCHAR(255),
  processed    BIT          NOT NULL,
  has_image    BIT          NOT NULL,
  published_at DATETIME,
  PRIMARY KEY (id)
)
