from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=['bcrypt'], deprecated='auto')

def forgot_employee_id(db: Session, name: str, email: str):
    return db.query(UserEmployee).filter((UserEmployee.name == name) & (UserEmployee.email == email)).first()

def forgot_employee_pw(db: Session, id: str, email: str):
    return db.query(UserEmployee).filter((UserEmployee.id == id) & (UserEmployee.email == email)).first()

def update_employee_pw(db: Session, id: str, new_pw: str):
    employee = db.query(UserEmployee).filter(UserEmployee.id == id).first()
    employee.password = pwd_context.hash(new_pw)
    db.add(employee)
    db.commit()

def forgot_manager_id(db: Session, name: str, email: str):
    return db.query(UserManager).filter((UserManager.name == name) & (UserManager.email == email)).first()

def forgot_manager_pw(db: Session, id: str, email: str):
    return db.query(UserManager).filter((UserManager.id == id) & (UserManager.email == email)).first()

def update_manager_pw(db: Session, id: str, new_pw: str):
    manager = db.query(UserManager).filter(UserManager.id == id).first()
    manager.password = pwd_context.hash(new_pw)
    db.add(manager)
    db.commit()
