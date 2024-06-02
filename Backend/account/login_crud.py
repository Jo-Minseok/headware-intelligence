from fastapi import Depends
from sqlalchemy.orm import Session
from db.db_connection import get_db
from db.models import UserEmployee, UserManager


def get_employee(id: str, type: str, db: Session = Depends(get_db)):
    if (type == "employee"):
        return db.query(UserEmployee).filter(UserEmployee.id == id).first()
    elif (type == "manager"):
        return db.query(UserManager).filter(UserManager.id == id).first()
