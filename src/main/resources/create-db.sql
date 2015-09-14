create table records ( id bigint not null generated always as identity (start with 1, increment by 1), type int not null, category varchar(100) not null, create_date date not null, description varchar(255), amount double not null);

create table categories (id bigint not null generated always as identity (start with 1, increment by 1), name varchar(100) not null, def integer default 0, report integer default 1, unique (name));

insert into categories (name, def, report) values ('Продукты', 1, 1);
