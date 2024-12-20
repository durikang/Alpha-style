-- 데이터 초기화 순서: 자식 -> 부모
-- 자식 테이블 데이터 삭제
DELETE FROM item_region_mapping;
DELETE FROM item_thumbnails;
DELETE FROM order_detail;
DELETE FROM orders;
DELETE FROM item;

-- 부모 테이블 데이터 삭제
DELETE FROM delivery_code;
DELETE FROM item_type;
DELETE FROM region;
DELETE FROM members;
DELETE FROM system_log;

-- DeliveryCode 초기 데이터
INSERT INTO delivery_code (id, code, display_name) VALUES (DELIVERY_CODE_SEQ.NEXTVAL, 'DROPOFF', '집하');
INSERT INTO delivery_code (id, code, display_name) VALUES (DELIVERY_CODE_SEQ.NEXTVAL, 'COMPLETE', '배송완료');

-- ItemType 초기 데이터
INSERT INTO item_type (id, code, description) VALUES (ITEM_TYPE_SEQ.NEXTVAL, 'BOOK', '도서');
INSERT INTO item_type (id, code, description) VALUES (ITEM_TYPE_SEQ.NEXTVAL, 'FOOD', '음식');

-- Region 초기 데이터
INSERT INTO region (id, code, display_name, active) VALUES (REGION_SEQ.NEXTVAL, 'SEOUL', '서울', 1);
INSERT INTO region (id, code, display_name, active) VALUES (REGION_SEQ.NEXTVAL, 'BUSAN', '부산', 1);
INSERT INTO region (id, code, display_name, active) VALUES (REGION_SEQ.NEXTVAL, 'DAEGU', '대구', 1);

-- Members 초기 데이터
INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (MEMBER_SEQ.NEXTVAL, 'userA', '홍길동', 'password123', 'userA@example.com', '010-1234-5678', '서울특별시 강남구', '12345', '아파트 101호', TO_DATE('1990-01-01', 'YYYY-MM-DD'), '가장 좋아하는 색은?', '파랑', 'customer');

INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (MEMBER_SEQ.NEXTVAL, 'system', '김철수', '1234', 'admin@example.com', '010-9876-5432', '부산광역시 해운대구', '67890', '오피스텔 202호', TO_DATE('1985-05-15', 'YYYY-MM-DD'), '가장 좋아하는 음식은?', '스테이크', 'admin');

-- Item 초기 데이터
INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (ITEM_SEQ.NEXTVAL, 'Book1', 15000, 20, 1, (SELECT id FROM item_type WHERE code = 'BOOK'),
        (SELECT id FROM delivery_code WHERE code = 'DROPOFF'),
        (SELECT user_no FROM members WHERE user_id = 'userA'));

INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (ITEM_SEQ.NEXTVAL, 'Food1', 30000, 15, 0, (SELECT id FROM item_type WHERE code = 'FOOD'),
        (SELECT id FROM delivery_code WHERE code = 'COMPLETE'),
        (SELECT user_no FROM members WHERE user_id = 'system'));

-- Item Region 관계 초기 데이터 (ManyToMany)
INSERT INTO item_region_mapping (item_id, region_id)
VALUES ((SELECT item_id FROM item WHERE item_name = 'Book1'), (SELECT id FROM region WHERE code = 'SEOUL'));

INSERT INTO item_region_mapping (item_id, region_id)
VALUES ((SELECT item_id FROM item WHERE item_name = 'Book1'), (SELECT id FROM region WHERE code = 'BUSAN'));

INSERT INTO item_region_mapping (item_id, region_id)
VALUES ((SELECT item_id FROM item WHERE item_name = 'Food1'), (SELECT id FROM region WHERE code = 'DAEGU'));

-- Item Thumbnails 초기 데이터
INSERT INTO item_thumbnails (item_id, upload_file_name, store_file_name)
VALUES ((SELECT item_id FROM item WHERE item_name = 'Book1'), 'book1_thumb.jpg', '2024-12/book1_thumb_123.jpg');

INSERT INTO item_thumbnails (item_id, upload_file_name, store_file_name)
VALUES ((SELECT item_id FROM item WHERE item_name = 'Food1'), 'food1_thumb.jpg', '2024-12/food1_thumb_456.jpg');

-- Orders 초기 데이터
INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (ORDER_SEQ.NEXTVAL, (SELECT user_no FROM members WHERE user_id = 'userA'), TO_DATE('2024-12-01', 'YYYY-MM-DD'), 45000, '배송완료');

INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (ORDER_SEQ.NEXTVAL, (SELECT user_no FROM members WHERE user_id = 'system'), TO_DATE('2024-12-02', 'YYYY-MM-DD'), 30000, '집하');

-- OrderDetail 초기 데이터
INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (ORDER_DETAIL_SEQ.NEXTVAL, (SELECT order_no FROM orders WHERE total_amount = 45000),
        (SELECT item_id FROM item WHERE item_name = 'Book1'), 2, 30000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (ORDER_DETAIL_SEQ.NEXTVAL, (SELECT order_no FROM orders WHERE total_amount = 45000),
        (SELECT item_id FROM item WHERE item_name = 'Food1'), 1, 15000);

-- System Log 초기 데이터
INSERT INTO system_log (log_id, entity_name, entity_id, action_type, performed_by, performed_date, details)
VALUES (SYSTEM_LOG_SEQ.NEXTVAL, 'Item', (SELECT item_id FROM item WHERE item_name = 'Book1'), 'CREATE', 'adminUser', CURRENT_TIMESTAMP, 'Item(Book1) created with price 15000 and quantity 20.');

INSERT INTO system_log (log_id, entity_name, entity_id, action_type, performed_by, performed_date, details)
VALUES (SYSTEM_LOG_SEQ.NEXTVAL, 'Order', (SELECT order_no FROM orders WHERE total_amount = 45000), 'CREATE', 'userA', CURRENT_TIMESTAMP, 'Order for Book1 created with quantity 2 and total 30000.');
