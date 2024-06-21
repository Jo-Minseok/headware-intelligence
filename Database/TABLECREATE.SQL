-- 회사 목록 테이블
CREATE TABLE companyList (company VARCHAR(100) PRIMARY KEY);
-- 안전 관리자 회원 테이블
CREATE TABLE userManager (
	id VARCHAR(100) PRIMARY KEY,
	password VARCHAR(100) NOT NULL,
	name VARCHAR(16) NOT NULL,
	email VARCHAR(100) UNIQUE NOT NULL,
	phoneNo VARCHAR(15) NOT NULL,
	company VARCHAR(100) NULL,
	alertToken VARCHAR(200) NULL,
	loginToken VARCHAR(200) NULL,
	FOREIGN KEY (company) REFERENCES companyList(company)
);
-- 작업 목록 테이블
CREATE TABLE workList (
	workId INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	company VARCHAR(100) NOT NULL,
	startDate DATE NOT NULL,
	endDate DATE NULL,
	managerId VARCHAR(100) NOT NULL,
	FOREIGN KEY (company) REFERENCES companyList(company),
	FOREIGN KEY (managerId) REFERENCES userManager(id)
);
-- 근로자 테이블
CREATE TABLE userEmployee (
	id VARCHAR(100) PRIMARY KEY,
	password VARCHAR(100) NOT NULL,
	name VARCHAR(16) NOT NULL,
	email VARCHAR(100) UNIQUE NOT NULL,
	phoneNo VARCHAR(15) NOT NULL,
	company VARCHAR(100) NULL,
	alertToken VARCHAR(200) NULL,
	loginToken VARCHAR(200) NULL,
	FOREIGN KEY (company) REFERENCES companyList(company)
);
-- 작업 참가 내역
CREATE TABLE work (
	workId INT NOT NULL,
	workerId VARCHAR(100) NOT NULL,
	PRIMARY KEY (workId, workerId),
	FOREIGN KEY (workId) REFERENCES workList(workId),
	FOREIGN KEY (workerId) REFERENCES userEmployee(id)
);
-- 사고 발생 테이블
CREATE TABLE accident (
	no INT AUTO_INCREMENT PRIMARY KEY,
	date DATE NOT NULL,
	time TIME NOT NULL,
	latitude DOUBLE NOT NULL DEFAULT 1.0,
	longitude DOUBLE NOT NULL DEFAULT 1.0,
	workId INT NOT NULL,
	victimId VARCHAR(100) NOT NULL,
	category VARCHAR(8) NOT NULL,
	CHECK (
		longitude >= -180.000000
		AND longitude <= 180.000000
	),
	CHECK (
		latitude >= -90.000000
		AND latitude <= 90.000000
	),
	FOREIGN KEY (workId, victimId) REFERENCES work(workId, workerId)
);
-- 처리 상황 테이블
CREATE TABLE accidentProcessing (
	no INT AUTO_INCREMENT,
	situation varchar(100) NOT NULL,
	date date NOT NULL,
	time time NOT NULL,
	detail varchar(100) NOT NULL,
	FOREIGN KEY (no) REFERENCES accident(no)
);