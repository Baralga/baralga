insert into db_version (version, description, created_at) values (3, 'Migrated to uuids and added primary keys.', CURRENT_TIMESTAMP());

alter table db_version add db_version_id varchar2(36);

update db_version
set db_version_id = RANDOM_UUID()
where db_version_id is null;

alter table db_version
drop column id;

ALTER TABLE db_version
ALTER db_version_id varchar2(36) NOT NULL;

ALTER TABLE db_version
ADD CONSTRAINT pk_db_version PRIMARY KEY (db_version_id);

ALTER TABLE db_version
ADD CONSTRAINT uk_version UNIQUE (version);

-- project
alter table project add project_id varchar2(36);

update project
set project_id = RANDOM_UUID()
where project_id is null;

-- activity
alter table activity add activity_id varchar2(36);

update activity
set activity_id = RANDOM_UUID()
where activity_id is null;

alter table activity
drop column id;

ALTER TABLE activity
ALTER activity_id varchar2(36) NOT NULL;

ALTER TABLE activity
ADD CONSTRAINT pk_activity PRIMARY KEY (activity_id);

-- link to projects
ALTER TABLE activity
RENAME COLUMN project_id TO project_id_identity;

alter table activity add project_id varchar2(36);

update activity a
set a.project_id = (select p.project_id from project p where p.id = a.project_id_identity)
where exists
(select * from project p where p.id = a.project_id_identity);

ALTER TABLE activity
ALTER project_id varchar2(36) NOT NULL;

-- finish projects
alter table activity
drop column project_id_identity;

alter table project
drop column id;

ALTER TABLE project
ALTER project_id varchar2(36) NOT NULL;

ALTER TABLE project
ADD CONSTRAINT pk_project PRIMARY KEY (project_id);

ALTER TABLE activity
ADD CONSTRAINT fk_activity_project
FOREIGN KEY (project_id) REFERENCES project (project_id);
