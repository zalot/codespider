drop table smovie;
create table smovie(
	name varchar(200),
	img varchar(200),
	simg  varchar(200),
	url varchar(200),
	type varchar(10),
	cate  varchar(10),
	src varchar(10),
	primary key(name)
);

insert into smovie(name,img,simg,url,src,type,cate) values('test','img','simg','http://test','18av','10','null')
select * from smovie where name like '%√¿÷Ï%'my