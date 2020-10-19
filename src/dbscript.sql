drop database Library_Mgt;
CREATE DATABASE Library_Mgt;
USE Library_Mgt;

CREATE TABLE IF NOT EXISTS book
(
    isbn    varchar(13) not null,
    title   varchar(50) null,
    author  varchar(50) null,
    edition varchar(50) null,
    primary key (isbn)
);

CREATE TABLE IF NOT EXISTS extra
(
    returnedDay int            not null,
    fee         decimal(10, 2) null,
    primary key (returnedDay)
);

CREATE TABLE IF NOT EXISTS member
(
    nic     varchar(10)  not null,
    name    varchar(50)  null,
    address varchar(100) null,
    primary key (nic)
);

CREATE TABLE IF NOT EXISTS borrow
(
    borrow_id varchar(10) not null,
    nic       varchar(10) null,
    isbn      varchar(13) null,
    date      date        not null,
    primary key (borrow_id),
    constraint fk_book
        foreign key (isbn) references book (isbn),
    constraint fk_member
        foreign key (nic) references member (nic)
);

CREATE TABLE IF NOT EXISTS `return`
(
    borrow_id varchar(10) not null,
    date      date        not null,
    primary key (borrow_id, date),
    constraint fk_borrowId
        foreign key (borrow_id) references borrow (borrow_id)
);

CREATE TABLE IF NOT EXISTS user
(
    userName varchar(20) not null,
    password varchar(30) not null,
    primary key (userName)
);

INSERT INTO library_mgt.book (isbn, title, author, edition) VALUES ('9780234589913', 'Harry Potter and the Chamber of Secrets', 'J.K. Rowling ', 'Second');
INSERT INTO library_mgt.book (isbn, title, author, edition) VALUES ('9780439139601', 'Harry Potter and the Goblet of Fire', 'J.K. Rowling ', 'First');
INSERT INTO library_mgt.book (isbn, title, author, edition) VALUES ('9780545791342', 'Harry Potter and the Prisoner of Azkaban', 'J.K. Rowling ', 'First');
INSERT INTO library_mgt.book (isbn, title, author, edition) VALUES ('9780747532743', 'Harry Potter and the Sorcerer''s Stone ', 'J.K. Rowling ', 'First');

INSERT INTO library_mgt.borrow (borrow_id, nic, isbn, date) VALUES ('B001', '123456789V', '9780234589913', '2020-03-18');
INSERT INTO library_mgt.borrow (borrow_id, nic, isbn, date) VALUES ('B002', '123456789V', '9780439139601', '2020-03-18');
INSERT INTO library_mgt.borrow (borrow_id, nic, isbn, date) VALUES ('B003', '234567891V', '9780545791342', '2020-03-10');

INSERT INTO library_mgt.extra (returnedDay, fee) VALUES (7, 250.00);
INSERT INTO library_mgt.extra (returnedDay, fee) VALUES (14, 500.00);
INSERT INTO library_mgt.extra (returnedDay, fee) VALUES (21, 750.00);
INSERT INTO library_mgt.extra (returnedDay, fee) VALUES (28, 1000.00);
INSERT INTO library_mgt.extra (returnedDay, fee) VALUES (30, 1250.00);

INSERT INTO library_mgt.member (nic, name, address) VALUES ('123456789V', 'Adam', 'Colombo');
INSERT INTO library_mgt.member (nic, name, address) VALUES ('234567891V', 'Lily', 'Galle');
INSERT INTO library_mgt.member (nic, name, address) VALUES ('345678912V', 'Rachel', 'Galle');
INSERT INTO library_mgt.member (nic, name, address) VALUES ('456789123V', 'Monica', 'Panadura');

INSERT INTO library_mgt.`return` (borrow_id, date) VALUES ('B001', '2020-03-22');

INSERT INTO library_mgt.user (userName, password) VALUES ('admin', '1234');

