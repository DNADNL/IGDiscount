# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups


alter table if exists image drop constraint if exists fk_image_product_id;

alter table if exists product drop constraint if exists fk_product_seller_id;

alter table if exists product drop constraint if exists fk_product_image_id;

alter table image add constraint fk_image_product_id foreign key (product_id) references product (id) on delete cascade;

alter table product add constraint fk_product_seller_id foreign key (seller_id) references seller_company (id) on delete cascade ;

alter table product add constraint fk_product_image_id foreign key (image_id) references image (id) on delete cascade;

# --- !Downs

