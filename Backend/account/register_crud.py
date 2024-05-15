from fastapi import Depends
from sqlalchemy.orm import Session
from db.db_connection import get_db
from account.account_schema import Account_Input_Create
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext


"""
해시 알고리즘을 설정하기 위한 함수
해싱 알고리즘 = bcrypt
deprecated = 이 설정이 자동으로 폐기될 때 경고 표시 여부
"""
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def create_account(input_data: Account_Input_Create, db: Session = Depends(get_db)):
    if (input_data.type == "employee"):
        db_data = UserEmployee(employee_id=input_data.id,
                               password=pwd_context.hash(
                                   input_data.password),
                               name=input_data.name,
                               email=input_data.email,
                               phone_no=input_data.phone_no,
                               company=input_data.company)
    elif (input_data.type == "manager"):
        db_data = UserManager(manager_id=input_data.id,
                              password=pwd_context.hash(input_data.password),
                              name=input_data.name,
                              email=input_data.email,
                              phone_no=input_data.phone_no,
                              company=input_data.company)
    db.add(db_data)
    db.commit()


# 근로자 계정 존재 여부 확인 함수
def get_existing_account(input_data: Account_Input_Create, db: Session = Depends(get_db)):
    # DB SELECT 이용. ID 또는 Email 존재 여부 확인
    if (input_data.type == "employee"):
        return db.query(UserEmployee).filter(
            (UserEmployee.id == input_data.id) | (
                UserEmployee.email == input_data.email)
        ).first()
    elif (input_data.type == "manager"):
        return db.query(UserManager).filter(
            (UserManager.id == input_data.id) | (
                UserManager.email == input_data.email)
        ).first()
