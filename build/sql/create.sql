-- create database jyrobot;

drop table jyro_retrieved_urls;
create table jyro_retrieved_urls(
	id         bigint unsigned auto_increment primary key,
	url        varchar(1024) not null,
	retrive_at timestamp,
	created_at timestamp
);

create table url_content(
	id      int auto_increment,
	url     varchar(1024) not null,
	title   varchar(1024) not null,
	summary varchar(2048) not null,
);

create table location(
	id int auto_increment,
	latitude  real not null,
	longitude real not null,
	altitude  real,
);