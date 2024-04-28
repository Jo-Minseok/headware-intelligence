from sqlalchemy.orm import Session
from account.account_schema import Employee_Create, Manager_Create
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext


"""
해시 알고리즘을 설정하기 위한 함수
해싱 알고리즘 = bcrypt
deprecated = 이 설정이 자동으로 폐기될 때 경고 표시 여부
"""
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


# 근로자 계정 생성 함수
def create_employee(db: Session, user_employee: Employee_Create):
    # ORM 근로자 객체 생성 + bcrypt 해쉬 알고리즘으로 비밀번호 암호화
    db_employee = UserEmployee(id=user_employee.id,
                               password=pwd_context.hash(
                                   user_employee.password),
                               name=user_employee.name,
                               email=user_employee.email,
                               manager=user_employee.manager,
                               phone_no=user_employee.phone_no,
                               company=user_employee.company)
    # DB INSERT
    db.add(db_employee)
    # DB Commit
    db.commit()


# 근로자 계정 존재 여부 확인 함수
def get_existing_employee(db: Session, user_employee: Employee_Create):
    # DB SELECT 이용. ID 또는 Email 존재 여부 확인
    return db.query(UserEmployee).filter(
        (UserEmployee.id == user_employee.id) | (
            UserEmployee.email == user_employee.email)
    ).first()


# 관리자 계정 생성 함수
def create_manager(db: Session, user_manager: Manager_Create):
    # ORM 관리자 객체 생성 + bcrypt 해쉬 알고리즘으로 비밀번호 암호화
    db_manager = UserManager(id=user_manager.id,
                             password=pwd_context.hash(user_manager.password),
                             name=user_manager.name,
                             email=user_manager.email,
                             company=user_manager.company)
    # DB INSERT
    db.add(db_manager)
    # DB Commit
    db.commit()


# 관리자 계정 존재 여부 확인 함수
def get_existing_manager(db: Session, user_manager: Manager_Create):
    # DB SELECT 이용. ID 또는 Email 존재 여부 확인
    return db.query(UserManager).filter(
        (UserManager.id == user_manager.id) | (
            UserManager.email == user_manager.email)
    ).first()
