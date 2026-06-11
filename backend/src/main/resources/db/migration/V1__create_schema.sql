-- FeatherLog core schema

CREATE TABLE birds (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    common_name           VARCHAR(100)   NOT NULL,
    scientific_name       VARCHAR(120)   NOT NULL,
    family                VARCHAR(80)    NOT NULL,
    conservation_status   VARCHAR(30)    NOT NULL,
    size_category         VARCHAR(20)    NOT NULL,
    average_length_cm     DECIMAL(6,1),
    average_weight_grams  DECIMAL(8,1),
    habitat               VARCHAR(255),
    diet                  VARCHAR(255),
    migratory             BOOLEAN        NOT NULL DEFAULT FALSE,
    description           TEXT,
    icon                  VARCHAR(10)    NOT NULL,
    color_hex             VARCHAR(7)     NOT NULL,
    created_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_birds_scientific_name UNIQUE (scientific_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE locations (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150)  NOT NULL,
    country       VARCHAR(100)  NOT NULL,
    country_code  VARCHAR(2)    NOT NULL,
    region        VARCHAR(100),
    habitat_type  VARCHAR(30)   NOT NULL,
    latitude      DECIMAL(9,6)  NOT NULL,
    longitude     DECIMAL(9,6)  NOT NULL,
    best_season   VARCHAR(120),
    description   TEXT,
    icon          VARCHAR(10)   NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sightings (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    bird_id            BIGINT       NOT NULL,
    location_id        BIGINT       NOT NULL,
    sighting_date      DATE         NOT NULL,
    sighting_time      TIME,
    quantity           INT          NOT NULL DEFAULT 1,
    observer_name      VARCHAR(100) NOT NULL,
    weather_condition  VARCHAR(20),
    notes              TEXT,
    favorite           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sightings_bird FOREIGN KEY (bird_id) REFERENCES birds (id),
    CONSTRAINT fk_sightings_location FOREIGN KEY (location_id) REFERENCES locations (id),
    CONSTRAINT chk_sightings_quantity CHECK (quantity >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sightings_bird_id ON sightings (bird_id);
CREATE INDEX idx_sightings_location_id ON sightings (location_id);
CREATE INDEX idx_sightings_date ON sightings (sighting_date);
CREATE INDEX idx_birds_family ON birds (family);
CREATE INDEX idx_birds_conservation_status ON birds (conservation_status);
CREATE INDEX idx_locations_country ON locations (country);
CREATE INDEX idx_locations_habitat_type ON locations (habitat_type);
