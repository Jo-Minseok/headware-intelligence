from fastapi import Depends
from sqlalchemy.orm import Session
from db.db_connection import get_db
from account.account_schema import Login_Input
from db.models import UserEmployee, UserManager


def get_employee(input_data: Login_Input, db: Session):
    if (input_data.type == "employee"):
        return db.query(UserEmployee).filter(UserEmployee.id == id).first()
    elif (input_data.type == "manager"):
        return db.query(UserManager).filter(UserManager.id == id).first()
