-- 데이터 초기화 순서: 자식 -> 부모
DELETE FROM item_region_mapping;
DELETE FROM item_thumbnails;
DELETE FROM order_detail;
DELETE FROM orders;
DELETE FROM item;
DELETE FROM delivery_code;
DELETE FROM item_type;
DELETE FROM region;
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

-- Members 초기 데이터 (관리자 및 userA 포함)
-- 자동 생성된 회원 데이터는 별도 PL/SQL 구문에서 생성됨

-- Item 초기 데이터
INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (ITEM_SEQ.NEXTVAL, 'Book1', 15000, 10, 1,
        (SELECT id FROM item_type WHERE code = 'BOOK'),
        (SELECT id FROM delivery_code WHERE code = 'DROPOFF'),
        (SELECT user_no FROM members WHERE user_id = 'userA'));

INSERT INTO item (item_id, item_name, price, quantity, open, item_type_id, delivery_code_id, user_no)
VALUES (ITEM_SEQ.NEXTVAL, 'Food1', 30000, 5, 1,
        (SELECT id FROM item_type WHERE code = 'FOOD'),
        (SELECT id FROM delivery_code WHERE code = 'COMPLETE'),
        (SELECT user_no FROM members WHERE user_id = 'system'));

-- Orders 초기 데이터
INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (ORDER_SEQ.NEXTVAL,
        (SELECT user_no FROM members WHERE user_id = 'userA'),
        TO_DATE('2024-12-01', 'YYYY-MM-DD'),
        60000, '배송완료');

INSERT INTO orders (order_no, user_no, order_date, total_amount, delivery_status)
VALUES (ORDER_SEQ.NEXTVAL,
        (SELECT user_no FROM members WHERE user_id = 'system'),
        TO_DATE('2024-12-02', 'YYYY-MM-DD'),
        30000, '집하');

-- OrderDetail 초기 데이터
INSERT INTO order_detail (order_detail_no, order_no, item_id, quantity, subtotal)
VALUES (ORDER_DETAIL_SEQ.NEXTVAL,
        (SELECT order_no FROM orders WHERE ROWNUM = 1 AND delivery_status = '배송완료'),
        (SELECT item_id FROM item WHERE item_name = 'Book1'),
        2, 30000);

INSERT INTO order_detail (order_detail_no, order_no, item_id, quantity, subtotal)
VALUES (ORDER_DETAIL_SEQ.NEXTVAL,
        (SELECT order_no FROM orders WHERE ROWNUM = 1 AND delivery_status = '배송완료'),
        (SELECT item_id FROM item WHERE item_name = 'Food1'),
        1, 30000);

INSERT INTO order_detail (order_detail_no, order_no, item_id, quantity, subtotal)
VALUES (ORDER_DETAIL_SEQ.NEXTVAL,
        (SELECT order_no FROM orders WHERE ROWNUM = 1 AND delivery_status = '집하'),
        (SELECT item_id FROM item WHERE item_name = 'Food1'),
        1, 30000);
