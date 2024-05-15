from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager


def forgot_id(name: str, email: str, type: str, db: Session):
    if (type == "employee"):
        return db.query(UserEmployee).filter((UserEmployee.name == name) & (UserEmployee.email == email)).first()
    elif (type == "manager"):
        return db.query(UserManager).filter((UserManager.name == name) & (UserManager.email == email)).first()


def search_account(id: str, phone_no: str, type: str, db: Session):
    if (type == "employee"):
        return db.query(UserEmployee).filter((UserEmployee.id == id) & (UserEmployee.phone_no == phone_no)).first()
    elif (type == "manager"):
        return db.query(UserManager).filter((UserManager.id == id) & (UserManager.phone_no == phone_no)).first()
