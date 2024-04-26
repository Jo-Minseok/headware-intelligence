from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager


def get_employee(db: Session, id: str):
    return db.query(UserEmployee).filter(UserEmployee.id == id).first()


def get_manager(db: Session, id: str):
    return db.query(UserManager).filter(UserManager.id == id).first()
