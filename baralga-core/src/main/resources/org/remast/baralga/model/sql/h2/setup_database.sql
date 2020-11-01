create table db_version (
     db_version_id varchar2(36) not null,
     version      number,
     created_at   timestamp,
     description  varchar2(255)
);

ALTER TABLE db_version
ADD CONSTRAINT pk_db_version PRIMARY KEY (db_version_id);

ALTER TABLE db_version
ADD CONSTRAINT uk_version UNIQUE (version);

create table project (
     project_id   varchar2(36) not null,
     title        varchar(255),
     description  varchar(4000),
     active       boolean
);

ALTER TABLE project
ADD CONSTRAINT pk_project PRIMARY KEY (project_id);

create table activity (
     activity_id  varchar2(36) not null,
     description  varchar(4000),
     start        timestamp,
     end          timestamp,
     project_id   varchar2(36) not null,
     FOREIGN key (project_id) REFERENCES project(project_id)
);

ALTER TABLE activity
ADD CONSTRAINT pk_activity PRIMARY KEY (activity_id);

ALTER TABLE activity
ADD CONSTRAINT fk_activity_project
FOREIGN KEY (project_id) REFERENCES project (project_id);

insert into db_version (db_version_id, version, description, created_at) values (RANDOM_UUID(), 3, 'Initial database setup.', CURRENT_TIMESTAMP());

