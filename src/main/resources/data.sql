TRUNCATE TABLE STATION;
TRUNCATE TABLE LINE;
TRUNCATE TABLE LINE_STATION;

insert into station values(1, '수원', CURRENT_TIMESTAMP());
insert into station values(2, '화서', CURRENT_TIMESTAMP());
insert into station values(3, '성균관대', CURRENT_TIMESTAMP());

insert into STATION values(4, '교대', CURRENT_TIMESTAMP());
insert into STATION values(5, '강남', CURRENT_TIMESTAMP());
insert into STATION values(6, '역삼', CURRENT_TIMESTAMP());
insert into STATION values(7, '선릉', CURRENT_TIMESTAMP());
insert into STATION values(8, '삼성', CURRENT_TIMESTAMP());
insert into STATION values(9, '종합운동장', CURRENT_TIMESTAMP());
insert into STATION values(10, '잠실새내', CURRENT_TIMESTAMP());
insert into STATION values(11, '잠실', CURRENT_TIMESTAMP());

insert into STATION values(12, '당고개', CURRENT_TIMESTAMP());
insert into STATION values(13, '상계', CURRENT_TIMESTAMP());
insert into STATION values(14, '노원', CURRENT_TIMESTAMP());
insert into STATION values(15, '창동', CURRENT_TIMESTAMP());
insert into STATION values(16, '쌍문', CURRENT_TIMESTAMP());

insert into LINE values(1, '1호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-blue-700');
insert into LINE values(2, '2호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-green-500');
insert into LINE values(3, '3호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-orange-500');
insert into LINE values(4, '4호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-blue-500');
insert into LINE values(5, '5호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-purple-500');
insert into LINE values(6, '6호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-yellow-500');
insert into LINE values(7, '7호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-green-500');
insert into LINE values(8, '8호선', '05:30', '23:30', 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bg-pink-500');

insert into LINE_STATION values(1, 0, 1, 1, 0, 0);
insert into LINE_STATION values(1, 1, 2, 1, 10, 2);
insert into LINE_STATION values(1, 2, 3, 2, 10, 2);

insert into LINE_STATION values(2, 0, 4, 4, 0, 0);
insert into LINE_STATION values(2, 1, 5, 4, 10, 2);
insert into LINE_STATION values(2, 2, 6, 5, 10, 2);
insert into LINE_STATION values(2, 3, 7, 6, 10, 2);
insert into LINE_STATION values(2, 4, 8, 7, 10, 2);
insert into LINE_STATION values(2, 5, 9, 8, 10, 2);
insert into LINE_STATION values(2, 6, 10, 9, 10, 2);
insert into LINE_STATION values(2, 7, 11, 10, 10, 2);