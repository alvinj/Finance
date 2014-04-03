
# Notes: 
# - `Ups` and `Downs` comments are important to Play.

# --- !Ups

--
-- TODO i think my problem is that each file should only contain changes
-- to the last file, i.e., new tables or corrections. 

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

--
-- TRANSACTIONS
--
-- type is B(uy) or S(ell)
create table transactions (
  id int auto_increment not null,
  symbol varchar(10) not null,
  ttype char(1) not null,
  quantity int not null,
  price decimal(10,2) not null,
  date_time timestamp not null default now(),
  notes text,
  primary key (id)
) engine = InnoDB;

-- skip the date_time field, let it default to now ('2014-03-26 18:35:44')
insert into transactions (symbol, ttype, quantity, price, notes) 
  values ('AAPL', 'B', 100, 525.0, 'News: Expect new AppleTV any day now.');


--
-- RESEARCH
--
create table research_links (
  id int auto_increment not null,
  symbol varchar(10) not null,
  url varchar(200) not null,
  date_time timestamp not null default now(),
  notes text,
  primary key (id)
) engine = InnoDB;

insert into research_links (symbol, url, notes) 
  values ('AAPL', 'http://foo.bar.com/buy-aapl-now', 'A good article on upcoming products.');


# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;
drop table if exists stock_prices;
drop table if exists users;
drop table if exists stocks;
drop table if exists transactions;
drop table if exists research_links;
SET FOREIGN_KEY_CHECKS = 1;










