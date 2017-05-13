drop table stop;
drop table bus;

create table bus(
	bus_id int not null primary key,
	f_beacon varchar(45) unique, -- 앞문의 비콘 아이디
	b_beacon varchar(45) unique, -- 뒷문의 비콘 아이디
	distance float, -- 비콘 사이의 거리
	ip varchar(20) unique, -- 버스 아두이노의 IP
	port int -- 버스 아두이노의 port
);

create table stop(
	stop_n int not null primary key,
	beacon varchar(45) unique
);