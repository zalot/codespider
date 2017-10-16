drop table avspider;
create table avspider(
	name varchar(200),
	aname varchar(200),
	simg  varchar(150),
	simg_local varchar(150),
	img varchar(150),
	img_local varchar(150),
	url varchar(300),
	svideo varchar(150),
	svideo_local varchar(150),
	video varchar(150),
	keywords varchar(100),
	nvyouname varchar(100),
	fanhao varchar(20),
	cate  varchar(40),
	src varchar(40),
	faxing varchar(100),
    zhizuo varchar(100),
    daoyan varchar(100),
    riqi varchar(100),
    detalURLs varchar(800),
	primary key(name,src)
);


select * from avspider where src = '18av'