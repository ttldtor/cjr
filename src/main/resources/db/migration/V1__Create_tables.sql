CREATE TABLE log_site (
    id IDENTITY PRIMARY KEY NOT NULL,
    name varchar UNIQUE,
    conference varchar DEFAULT '' NOT NULL,
    url varchar DEFAULT '' NOT NULL
);

CREATE TABLE event_type (
    id IDENTITY PRIMARY KEY NOT NULL,
    name varchar UNIQUE
);

INSERT INTO event_type (name) VALUES ('Enter'), ('Exit'), ('Message'), ('ThirdPersonMessage');

CREATE TABLE event (
    id IDENTITY PRIMARY KEY NOT NULL,
    type_id bigint NOT NULL,
    message varchar DEFAULT '' NOT NULL,
    who varchar DEFAULT '' NOT NULL,
    "timestamp" bigint NOT NULL,
    CONSTRAINT event_event_type_id FOREIGN KEY (type_id) REFERENCES event_type (id) ON DELETE CASCADE ON UPDATE CASCADE
);