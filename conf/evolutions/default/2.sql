
# Notes: 
# - `Ups` and `Downs` comments are important to Play.


# --- !Ups

create table users (
  id int not null auto_increment,
  name varchar(100) not null,
  username varchar(20) not null,
  password varchar(100) not null,
  email varchar(100) not null,
  primary key (id),
  constraint unique index idx_username_unique (username asc)
) engine = InnoDB;

--
-- TODO need to add 'user_id' to this table
--
create table stocks (
  id int auto_increment not null,
  symbol varchar(10) not null,
  company varchar(32) not null,
  primary key (id),
  constraint unique index idx_stock_unique (symbol)
) engine = InnoDB;

insert into stocks (symbol, company) values ('AAPL', 'Apple');
insert into stocks (symbol, company) values ('GOOG', 'Google');

# create table stock_prices (
#   id int auto_increment not null,
#   stock_id int not null,
#   date_time timestamp not null default now(),
#   price decimal(15,2) not null default 0,
#   primary key (id),
#   foreign key (stock_id) references stocks (id) on delete cascade
# );


# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;
drop table if exists stock_prices;
drop table if exists users;
drop table if exists stocks;
SET FOREIGN_KEY_CHECKS = 1;


