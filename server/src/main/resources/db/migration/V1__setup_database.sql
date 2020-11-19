create table project (
     project_id   varchar(36) not null,
     title        varchar(255),
     description  varchar(4000),
     active       boolean
);

ALTER TABLE project
ADD CONSTRAINT pk_project PRIMARY KEY (project_id);

create table activity (
     activity_id  varchar(36) not null,
     description  varchar(4000),
     start        timestamp,
     end          timestamp,
     project_id   varchar(36) not null,
     FOREIGN key (project_id) REFERENCES project(project_id)
);

ALTER TABLE activity
ADD CONSTRAINT pk_activity PRIMARY KEY (activity_id);

ALTER TABLE activity
ADD CONSTRAINT fk_activity_project
FOREIGN KEY (project_id) REFERENCES project (project_id);
