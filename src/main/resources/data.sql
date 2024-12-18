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
INSERT INTO delivery_code (id, code, display_name) VALUES (1, 'DROPOFF', '집하');
INSERT INTO delivery_code (id, code, display_name) VALUES (2, 'COMPLETE', '배송완료');

-- ItemType 초기 데이터
INSERT INTO item_type (id, code, description) VALUES (1, 'BOOK', '도서');
INSERT INTO item_type (id, code, description) VALUES (2, 'FOOD', '음식');

-- Region 초기 데이터
INSERT INTO region (id, code, display_name, active) VALUES (1, 'SEOUL', '서울', 1);
INSERT INTO region (id, code, display_name, active) VALUES (2, 'BUSAN', '부산', 1);
INSERT INTO region (id, code, display_name, active) VALUES (3, 'DAEGU', '대구', 1);

-- Members 초기 데이터
INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (1, 'userA', '홍길동', 'password123', 'userA@example.com', '010-1234-5678', '서울특별시 강남구', '12345', '아파트 101호', TO_DATE('1990-01-01', 'YYYY-MM-DD'), '가장 좋아하는 색은?', '파랑', 'customer');

INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (2, 'system', '김철수', '1234', 'admin@example.com', '010-9876-5432', '부산광역시 해운대구', '67890', '오피스텔 202호', TO_DATE('1985-05-15', 'YYYY-MM-DD'), '가장 좋아하는 음식은?', '스테이크', 'admin');

-- Item 초기 데이터
INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (1, 'Book1', 15000, 20, 1, 1, 1, 1);

INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (2, 'Food1', 30000, 15, 0, 2, 2, 2);

-- Item Region 관계 초기 데이터 (ManyToMany)
INSERT INTO item_region_mapping (item_id, region_id) VALUES (1, 1);
INSERT INTO item_region_mapping (item_id, region_id) VALUES (1, 2);
INSERT INTO item_region_mapping (item_id, region_id) VALUES (2, 3);

-- Item Thumbnails 초기 데이터
INSERT INTO item_thumbnails (item_id, upload_file_name, store_file_name)
VALUES (1, 'book1_thumb.jpg', '2024-12/book1_thumb_123.jpg');

INSERT INTO item_thumbnails (item_id, upload_file_name, store_file_name)
VALUES (2, 'food1_thumb.jpg', '2024-12/food1_thumb_456.jpg');

-- Orders 초기 데이터
INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (1, 1, TO_DATE('2024-12-01', 'YYYY-MM-DD'), 45000, '배송완료');

INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (2, 2, TO_DATE('2024-12-02', 'YYYY-MM-DD'), 30000, '집하');

-- OrderDetail 초기 데이터
INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (1, 1, 1, 2, 30000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (2, 1, 2, 1, 15000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (3, 2, 1, 1, 15000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (4, 2, 2, 1, 15000);

-- System Log 초기 데이터
INSERT INTO system_log (log_id, entity_name, entity_id, action_type, performed_by, performed_date, details)
VALUES (1, 'Item', 1, 'CREATE', 'adminUser', CURRENT_TIMESTAMP, 'Item(Book1) created with price 15000 and quantity 20.');

INSERT INTO system_log (log_id, entity_name, entity_id, action_type, performed_by, performed_date, details)
VALUES (2, 'Order', 1, 'CREATE', 'userA', CURRENT_TIMESTAMP, 'Order for Book1 created with quantity 2 and total 30000.');
