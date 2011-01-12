create table db_version (
     id           identity,
     version      number,
     created_at   timestamp,
     description  varchar2(255)
);
    
create table project (
     id           identity,
     title        varchar(255),
     description  varchar(4000),
     active       boolean
);

create table activity (
     id           identity,
     description  varchar(4000),
     start        timestamp,
     end          timestamp,
     project_id   number,
     FOREIGN key (project_id) REFERENCES project(id)
);

insert into db_version (version, description) values (1, 'Initial database setup.');

