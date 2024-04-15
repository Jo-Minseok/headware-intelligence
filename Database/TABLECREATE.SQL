CREATE TABLE user_employee (
	'id' varchar2(100)	NOT NULL,
	'password' varchar2(100)	NOT NULL,
	'email'	varchar2(100)	NULL,
	'manager'	varchar2(100)	NOT NULL,
	'helmet_no'	varchar2(100)	NOT NULL,
	'phone_no'	varchar2(100)	NOT NULL
);

CREATE TABLE 'user_manager' (
	'id'	varchar2(100)	NOT NULL,
	'password'	varchar2(100)	NOT NULL,
	'email'	varchar2(100)	NULL,
	'company'	varchar2(100)	NOT NULL
);

CREATE TABLE 'accident' (
	'no'	int	NOT NULL	DEFAULT 1,
	'date'	date	NOT NULL,
	'time'	time	NOT NULL,
	'latitude'	float(3)	NOT NULL	DEFAULT 1,
	'longitude'	float(3)	NOT NULL	DEFAULT 1,
	'victim_id'	varchar2(100)	NOT NULL,
	'category'	varchar2(100)	NOT NULL
);

CREATE TABLE 'accident_proccessing' (
	'no'	int	NOT NULL	DEFAULT 1,
	'situation'	varchar2(100)	NOT NULL,
	'date'	date	NOT NULL,
	'time'	time	NOT NULL,
	'detail'	varchar2(100)	NOT NULL
);

CREATE TABLE 'company_list' (
	'company'	varchar2(100)	NOT NULL
);

ALTER TABLE 'user_employee' ADD CONSTRAINT 'PK_USER_EMPLOYEE' PRIMARY KEY (
	'id'
);

ALTER TABLE 'user_manager' ADD CONSTRAINT 'PK_USER_MANAGER' PRIMARY KEY (
	'id'
);

ALTER TABLE 'accident' ADD CONSTRAINT 'PK_ACCIDENT' PRIMARY KEY (
	'no'
);

ALTER TABLE 'accident_proccessing' ADD CONSTRAINT 'PK_ACCIDENT_PROCCESSING' PRIMARY KEY (
	'no'
);

ALTER TABLE 'company_list' ADD CONSTRAINT 'PK_COMPANY_LIST' PRIMARY KEY (
	'company'
);

ALTER TABLE 'accident_proccessing' ADD CONSTRAINT 'FK_accident_TO_accident_proccessing_1' FOREIGN KEY (
	'no'
)
REFERENCES 'accident' (
	'no'
);

