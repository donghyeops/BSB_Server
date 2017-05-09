drop table stop;
drop table bus;

create table bus(
	bus_id int(11) not null primary key,
	f_beacon varchar(45) unique, -- 앞문의 비콘 아이디
	b_beacon varchar(45) unique, -- 뒷문의 비콘 아이디
	distance int(10) -- 비콘 사이의 거리
);

create table stop(
	stop_n int(11) not null primary key,
	beacon varchar(45) unique
);