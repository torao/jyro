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

drop table if exists jyrobot_sessions;
create table jyrobot_sessions(
	id        bigint unsigned auto_increment not null primary key,
	scheme    varchar(32)  not null,
	host      varchar(256) not null,
	port      integer      not null,
	appid     varchar(64),
	priority  real not null default 0.0,
	accessed  timestamp,
	activated timestamp,
	created   timestamp not null
);

drop table if exists jyrobot_requests;
create table jyrobot_requests(
	id         integer auto_increment not null primary key,
	session_id bigint not null,
	path       varchar(2083) not null,
	response   integer,
	accessed   timestamp
);

drop table if exists jyrobot_cookies;
create table jyrobot_cookies(
	id integer auto_increment not null primary key,
	session_id bigint not null,
	name varchar(1024) not null,
	value varchar(1024) not null,
	domain varchar(1024),
	path varchar(1024),
	expires timestamp,
	secure boolean not null default false
);
