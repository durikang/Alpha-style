-- 자식 테이블 데이터 삭제
DELETE FROM order_detail;
DELETE FROM orders;
DELETE FROM item_region_mapping;
DELETE FROM item;

-- 부모 테이블 데이터 삭제
DELETE FROM delivery_code;
DELETE FROM item_type;
DELETE FROM region;
DELETE FROM members;


-- DeliveryCode 초기 데이터
INSERT INTO delivery_code (id, code, display_name) VALUES (1, 'DROPOFF', '집하');
INSERT INTO delivery_code (id, code, display_name) VALUES (2, 'COMPLETE', '배송완료');

-- ItemType 초기 데이터
INSERT INTO item_type (id, code, description) VALUES (1, 'BOOK', '도서');
INSERT INTO item_type (id, code, description) VALUES (2, 'FOOD', '음식');

-- Region 초기 데이터 (true 대신 1 사용)
INSERT INTO region (id, code, display_name, active) VALUES (1, 'SEOUL', '서울', 1);
INSERT INTO region (id, code, display_name, active) VALUES (2, 'BUSAN', '부산', 1);
INSERT INTO region (id, code, display_name, active) VALUES (3, 'DAEGU', '대구', 1);

-- Item 초기 데이터 (true/false 대신 1/0 사용)
INSERT INTO item (id, item_name, price, quantity, open, item_type_id, delivery_code_id)
VALUES (1, 'itemA', 10000, 10, 1, 1, 1);

INSERT INTO item (id, item_name, price, quantity, open, item_type_id, delivery_code_id)
VALUES (2, 'itemB', 20000, 20, 0, 2, 2);

-- Item과 Region 관계 (ManyToMany)
INSERT INTO item_region_mapping (item_id, region_id) VALUES (1, 1); -- itemA - 서울
INSERT INTO item_region_mapping (item_id, region_id) VALUES (1, 2); -- itemA - 부산
INSERT INTO item_region_mapping (item_id, region_id) VALUES (2, 2); -- itemB - 부산
INSERT INTO item_region_mapping (item_id, region_id) VALUES (2, 3); -- itemB - 대구

-- Member 초기 데이터
INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (1, 'userA', '홍길동', 'password123', 'userA@example.com', '010-1234-5678', '서울특별시 강남구', '12345', '아파트 101호', TO_DATE('1990-01-01', 'YYYY-MM-DD'), '가장 좋아하는 색은?', '파랑', 'customer');

INSERT INTO members (user_no, user_id, username, password, email, mobile_phone, address, zip_code, secondary_address, birth_date, security_question, security_answer, role)
VALUES (2, 'system', '김철수', '1234', 'userB@example.com', '010-9876-5432', '부산광역시 해운대구', '67890', '오피스텔 202호', TO_DATE('1985-05-15', 'YYYY-MM-DD'), '가장 좋아하는 음식은?', '스테이크', 'admin');

-- Order 초기 데이터
INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (1, 1, TO_DATE('2024-06-01', 'YYYY-MM-DD'), 30000, '배송완료');

INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (2, 2, TO_DATE('2024-06-02', 'YYYY-MM-DD'), 50000, '집하');

-- OrderDetail 초기 데이터
INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (1, 1, 1, 2, 20000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (2, 1, 2, 1, 20000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (3, 2, 1, 1, 10000);

INSERT INTO order_detail (order_detail_no, order_no, product_no, quantity, subtotal)
VALUES (4, 2, 2, 2, 40000);
