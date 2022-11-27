create table user_accounts
(
    username   varchar(50)                              not null
        primary key,
    password   varchar(60)                              not null,
    authority  enum ('ROLE_ADMINISTRATOR', 'ROLE_USER') not null,
    first_name varchar(75)                              not null,
    last_name  varchar(75)                              not null,
    email      varchar(150)                             not null,
    address    varchar(200)                             not null,
    constraint email
        unique (email)
);