-- 데이터 초기화 순서: 자식 -> 부모
DELETE FROM item_region_mapping;
DELETE FROM item_thumbnails;
DELETE FROM order_detail;
DELETE FROM orders;
DELETE FROM item;
DELETE FROM delivery_code;
DELETE FROM item_type;
DELETE FROM region;
DELETE FROM members;
DELETE FROM system_log;

-- DeliveryCode 초기 데이터
INSERT INTO delivery_code (id, code, display_name) VALUES (DELIVERY_CODE_SEQ.NEXTVAL, 'DROPOFF', '집하');
INSERT INTO delivery_code (id, code, display_name) VALUES (DELIVERY_CODE_SEQ.NEXTVAL, 'COMPLETE', '배송완료');
INSERT INTO delivery_code (id, code, display_name) VALUES (DELIVERY_CODE_SEQ.NEXTVAL, 'CANCELED', '취소됨');

-- ItemType 초기 데이터
INSERT INTO item_type (id, code, description) VALUES (ITEM_TYPE_SEQ.NEXTVAL, 'BOOK', '도서');
INSERT INTO item_type (id, code, description) VALUES (ITEM_TYPE_SEQ.NEXTVAL, 'FOOD', '음식');
INSERT INTO item_type (id, code, description) VALUES (ITEM_TYPE_SEQ.NEXTVAL, 'ELECTRONIC', '전자제품');

-- Region 초기 데이터
INSERT INTO region (id, code, display_name, active) VALUES (REGION_SEQ.NEXTVAL, 'SEOUL', '서울', 1);
INSERT INTO region (id, code, display_name, active) VALUES (REGION_SEQ.NEXTVAL, 'BUSAN', '부산', 1);
INSERT INTO region (id, code, display_name, active) VALUES (REGION_SEQ.NEXTVAL, 'DAEGU', '대구', 1);

-- Members 초기 데이터 (관리자 포함)
INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (MEMBER_SEQ.NEXTVAL, 'system', '김철수', '1234', 'admin@example.com', '010-9876-5432', '부산광역시 해운대구', '67890', '오피스텔 202호', TO_DATE('1985-05-15', 'YYYY-MM-DD'), '가장 좋아하는 음식은?', '스테이크', 'admin');

INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (MEMBER_SEQ.NEXTVAL, 'userA', '홍길동', 'password123', 'userA@example.com', '010-1234-5678', '서울특별시 강남구', '12345', '101호', TO_DATE('1990-01-01', 'YYYY-MM-DD'), '가장 좋아하는 색은?', '파랑', 'customer');

-- Item 초기 데이터
INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (ITEM_SEQ.NEXTVAL, 'Book1', 15000, 10, 1, (SELECT id FROM item_type WHERE code = 'BOOK'),
        (SELECT id FROM delivery_code WHERE code = 'DROPOFF'),
        (SELECT user_no FROM members WHERE user_id = 'userA'));

INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (ITEM_SEQ.NEXTVAL, 'Food1', 30000, 5, 1, (SELECT id FROM item_type WHERE code = 'FOOD'),
        (SELECT id FROM delivery_code WHERE code = 'COMPLETE'),
        (SELECT user_no FROM members WHERE user_id = 'system'));

-- Orders 초기 데이터
INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (1, (SELECT user_no FROM members WHERE user_id = 'userA'), TO_DATE('2024-12-01', 'YYYY-MM-DD'),
        60000, '배송완료');

INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (2, (SELECT user_no FROM members WHERE user_id = 'system'), TO_DATE('2024-12-02', 'YYYY-MM-DD'),
        30000, '집하');

-- OrderDetail 초기 데이터
INSERT INTO order_detail (order_detail_no, order_no, item_id, quantity, subtotal)
VALUES (1, 1, (SELECT item_id FROM item WHERE item_name = 'Book1'), 2, 30000);

INSERT INTO order_detail (order_detail_no, order_no, item_id, quantity, subtotal)
VALUES (2, 1, (SELECT item_id FROM item WHERE item_name = 'Food1'), 1, 30000);

INSERT INTO order_detail (order_detail_no, order_no, item_id, quantity, subtotal)
VALUES (3, 2, (SELECT item_id FROM item WHERE item_name = 'Food1'), 1, 30000);
