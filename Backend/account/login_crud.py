from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager


# 근로자 로그인 SELECT SQL ORM 형식
def get_employee(db: Session, id: str):
    return db.query(UserEmployee).filter(UserEmployee.employee_id == id).first()


# 관리자 로그인 SELECT SQL ORM 형식
def get_manager(db: Session, id: str):
    return db.query(UserManager).filter(UserManager.manager_id == id).first()
