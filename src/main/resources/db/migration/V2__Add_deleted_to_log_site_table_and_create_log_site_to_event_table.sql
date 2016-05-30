ALTER TABLE log_site
ADD IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE event RENAME CONSTRAINT event_event_type_id TO event_event_type_id_fk;

CREATE TABLE log_site_to_event (
    id IDENTITY PRIMARY KEY NOT NULL,
    log_site_id bigint NOT NULL,
    event_id bigint NOT NULL,
    CONSTRAINT log_site_to_event_log_site_id_fk FOREIGN KEY (log_site_id) REFERENCES log_site (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT log_site_to_event_event_id_fk FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE ON UPDATE CASCADE
);

