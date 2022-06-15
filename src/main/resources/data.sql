
insert into tag(tag_id, created_date, modified_date, name) values(1, now(), now(), 'spring');
insert into tag(tag_id, created_date, modified_date, name) values(2, now(), now(), 'java');

insert into user (created_date, modified_date, auth_provider, nickname, role, social_login_id) values (now(), now(), 'google', 'y1', 'USER', '123');
insert into user_tech_stack (created_date, modified_date, tag_id, user_id) values (now(), now(), 2, 1);
insert into user_tech_stack (created_date, modified_date, tag_id, user_id) values (now(), now(), 1, 1);



insert into post(created_date, modified_date, content, like_count, status, title, view_count, user_id) values(now(), now(), 'content1', 1, 'RECRUITING', 'title1', 1, 1);
insert into post(created_date, modified_date, content, like_count, status, title, view_count, user_id) values(now(), now(), 'content2', 1, 'RECRUITING', 'title2', 1, 1);


insert into POST_TECH_STACK(created_date, modified_date, tag_id, post_id) values(now(), now(), 1, 1);
insert into POST_TECH_STACK(created_date, modified_date, tag_id, post_id) values(now(), now(), 2, 2);