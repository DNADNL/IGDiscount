# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table admin (
  id                            bigint auto_increment not null,
  email                         varchar(255),
  password                      varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  token_authentification_id     bigint,
  token_reinitialisation_email_id bigint,
  constraint uq_admin_email unique (email),
  constraint uq_admin_token_authentification_id unique (token_authentification_id),
  constraint uq_admin_token_reinitialisation_email_id unique (token_reinitialisation_email_id),
  constraint pk_admin primary key (id)
);

create table seller_company (
  id                            bigint auto_increment not null,
  email                         varchar(255),
  password                      varchar(255),
  postal_code                   varchar(255),
  street                        varchar(255),
  city                          varchar(255),
  street_number                 varchar(255),
  siret                         varchar(255),
  company_name                  varchar(255),
  token_authentification_id     bigint,
  token_reinitialisation_email_id bigint,
  constraint uq_seller_company_email unique (email),
  constraint uq_seller_company_token_authentification_id unique (token_authentification_id),
  constraint uq_seller_company_token_reinitialisation_email_id unique (token_reinitialisation_email_id),
  constraint pk_seller_company primary key (id)
);

create table simple_user (
  id                            bigint auto_increment not null,
  email                         varchar(255),
  password                      varchar(255),
  postal_code                   varchar(255),
  street                        varchar(255),
  city                          varchar(255),
  street_number                 varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  token_authentification_id     bigint,
  token_reinitialisation_email_id bigint,
  constraint uq_simple_user_email unique (email),
  constraint uq_simple_user_token_authentification_id unique (token_authentification_id),
  constraint uq_simple_user_token_reinitialisation_email_id unique (token_reinitialisation_email_id),
  constraint pk_simple_user primary key (id)
);

create table token (
  id                            bigint auto_increment not null,
  expiration_date               timestamp,
  token                         varchar(255),
  constraint uq_token_token unique (token),
  constraint pk_token primary key (id)
);

alter table admin add constraint fk_admin_token_authentification_id foreign key (token_authentification_id) references token (id) on delete restrict on update restrict;

alter table admin add constraint fk_admin_token_reinitialisation_email_id foreign key (token_reinitialisation_email_id) references token (id) on delete restrict on update restrict;

alter table seller_company add constraint fk_seller_company_token_authentification_id foreign key (token_authentification_id) references token (id) on delete restrict on update restrict;

alter table seller_company add constraint fk_seller_company_token_reinitialisation_email_id foreign key (token_reinitialisation_email_id) references token (id) on delete restrict on update restrict;

alter table simple_user add constraint fk_simple_user_token_authentification_id foreign key (token_authentification_id) references token (id) on delete restrict on update restrict;

alter table simple_user add constraint fk_simple_user_token_reinitialisation_email_id foreign key (token_reinitialisation_email_id) references token (id) on delete restrict on update restrict;


# --- !Downs

alter table admin drop constraint if exists fk_admin_token_authentification_id;

alter table admin drop constraint if exists fk_admin_token_reinitialisation_email_id;

alter table seller_company drop constraint if exists fk_seller_company_token_authentification_id;

alter table seller_company drop constraint if exists fk_seller_company_token_reinitialisation_email_id;

alter table simple_user drop constraint if exists fk_simple_user_token_authentification_id;

alter table simple_user drop constraint if exists fk_simple_user_token_reinitialisation_email_id;

drop table if exists admin;

drop table if exists seller_company;

drop table if exists simple_user;

drop table if exists token;

