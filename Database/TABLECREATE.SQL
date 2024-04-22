-- 직원 테이블
CREATE TABLE user_employee (
    id varchar(100) PRIMARY KEY,
    password varchar(100) NOT NULL,
    email varchar(100) NULL,
    manager varchar(100) NOT NULL,
    helmet_no varchar(100) NOT NULL,
    phone_no varchar(100) NOT NULL,
    company varchar(100) NOT NULL
);
-- 관리자 테이블
CREATE TABLE user_manager (
    id varchar(100) PRIMARY KEY,
    password varchar(100) NOT NULL,
    email varchar(100) NULL,
    company varchar(100) NOT NULL
);


-- 사고 테이블
CREATE TABLE accident (
    no bigint AUTO_INCREMENT PRIMARY KEY,
    date date NOT NULL,
    time time NOT NULL,
    latitude float(6) NOT NULL DEFAULT 1,
    longitude float(6) NOT NULL DEFAULT 1,
    victim_id varchar(100) NOT NULL,
    category varchar(100) NOT NULL,
    CONSTRAINT ck_longitude CHECK (longitude >= -180.000000 AND longitude <= 180.000000),
    CONSTRAINT ck_latitude CHECK (latitude >= -90.000000 AND latitude <= 90.000000)
);


-- 사고 처리 테이블
CREATE TABLE accident_processing (
    no bigint AUTO_INCREMENT PRIMARY KEY,
    situation varchar(100) NOT NULL,
    date date NOT NULL,
    time time NOT NULL,
    detail varchar(100) NOT NULL,
    FOREIGN KEY (no) REFERENCES accident(no)
);
-- 회사 목록 테이블
CREATE TABLE company_list (
    company varchar(100) PRIMARY KEY
);

-- 관리자 & 회사 이름 외래키
ALTER TABLE user_manager ADD CONSTRAINT FK_company_list_TO_user_manager FOREIGN KEY(company) REFERENCES company_list (company);

-- 근로자 & 회사 이름 외래키
ALTER TABLE user_employee_company ADD CONSTRAINT FK_company_list_TO_user_employee FOREIGN KEY(company) REFERENCES company_list(company);

-- 관리자 & 회원 관리자 외래키
ALTER TABLE user_employee ADD CONSTRAINT FK_manager_TO_employee FOREIGN KEY (manager) REFERENCES user_manager (id);

-- 근로자 & 사고발생 외래키
ALTER TABLE accident ADD CONSTRAINT FK_employee_TO_accident FOREIGN KEY(victim_id) REFERENCES user_employee (id);

