DROP TABLE IF EXISTS ORDER_DETAIL CASCADE CONSTRAINTS;  -- OrderDetail 테이블 삭제
DROP TABLE IF EXISTS ORDERS CASCADE CONSTRAINTS;        -- Orders 테이블 삭제
DROP TABLE IF EXISTS ITEM CASCADE CONSTRAINTS;          -- Item 테이블 삭제
DROP TABLE IF EXISTS SUB_CATEGORY CASCADE CONSTRAINTS;  -- SubCategory 테이블 삭제
DROP TABLE IF EXISTS MAIN_CATEGORY CASCADE CONSTRAINTS; -- MainCategory 테이블 삭제
DROP TABLE IF EXISTS ITEM_TYPE CASCADE CONSTRAINTS;     -- ItemType 테이블 삭제
DROP TABLE IF EXISTS DELIVERY_CODE CASCADE CONSTRAINTS; -- DeliveryCode 테이블 삭제
DROP TABLE IF EXISTS REGION CASCADE CONSTRAINTS;        -- Region 테이블 삭제
DROP TABLE IF EXISTS MEMBERS CASCADE CONSTRAINTS;       -- Members 테이블 삭제
DROP TABLE IF EXISTS TAX_TYPE CASCADE CONSTRAINTS;      -- TaxType 테이블 삭제
DROP TABLE IF EXISTS FINANCIAL_DETAILS cascade constraints;
DROP TABLE IF EXISTS FINANCIAL_RECORD cascade constraints;
DROP TABLE IF EXISTS ITEM_REGION_MAPPING cascade constraints;
DROP TABLE IF EXISTS SYSTEM_LOG cascade constraints;
DROP TABLE IF EXISTS UPLOAD_FILE cascade constraints;


DROP SEQUENCE IF EXISTS ORDER_SEQ;          -- Order 시퀀스 삭제
DROP SEQUENCE IF EXISTS ORDER_DETAIL_SEQ;   -- OrderDetail 시퀀스 삭제
DROP SEQUENCE IF EXISTS ITEM_SEQ;           -- Item 시퀀스 삭제
DROP SEQUENCE IF EXISTS REGION_SEQ;         -- Region 시퀀스 삭제
DROP SEQUENCE IF EXISTS MAIN_CATEGORY_SEQ;  -- MainCategory 시퀀스 삭제
DROP SEQUENCE IF EXISTS SUB_CATEGORY_SEQ;   -- SubCategory 시퀀스 삭제
DROP SEQUENCE IF EXISTS ITEM_TYPE_SEQ;      -- ItemType 시퀀스 삭제
DROP SEQUENCE IF EXISTS DELIVERY_CODE_SEQ;  -- DeliveryCode 시퀀스 삭제
DROP SEQUENCE IF EXISTS MEMBERS_SEQ;        -- Members 시퀀스 삭제
DROP SEQUENCE IF EXISTS TAX_SEQ;       -- TaxType 시퀀스 삭제
DROP SEQUENCE IF EXISTS FINANCIAL_DETAILS_SEQ;
DROP SEQUENCE IF EXISTS FINANCIAL_RECORD_SEQ;
DROP SEQUENCE IF EXISTS UPLOAD_FILE_SEQ;
DROP SEQUENCE IF EXISTS SYSTEM_LOG_SEQ;

-- DeliveryCode 초기 데이터
-- 자동 생성된 DeliveryCode 데이터는 별도 PL/SQL 구문에서 생성됨

-- ItemType 초기 데이터
-- 자동 생성된 ItemType 데이터는 별도 PL/SQL 구문에서 생성됨

-- Region 초기 데이터
-- 자동 생성된 Region 데이터는 별도 PL/SQL 구문에서 생성됨

-- Members 초기 데이터 (관리자 및 userA 포함)
-- 자동 생성된 회원 데이터는 별도 PL/SQL 구문에서 생성됨

-- Item 초기 데이터
-- 자동 생성된 Item 데이터는 별도 PL/SQL 구문에서 생성됨

-- Orders 초기 데이터
-- 자동 생성된 Orders 데이터는 별도 PL/SQL 구문에서 생성됨

-- OrderDetail 초기 데이터
-- 자동 생성된 OrderDetail 데이터는 별도 PL/SQL 구문에서 생성됨
