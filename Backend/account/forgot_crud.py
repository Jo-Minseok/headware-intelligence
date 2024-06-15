from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager
from typing import Union
from db.db_connection import get_db
from fastapi import Depends


class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def find_user_by_name_and_email(self, name: str, email: str, user_type: str) -> Union[UserEmployee, UserManager, None]:
        if user_type == "employee":
            return self.db.query(UserEmployee).filter(UserEmployee.name == name, UserEmployee.email == email).first()
        elif user_type == "manager":
            return self.db.query(UserManager).filter(UserManager.name == name, UserManager.email == email).first()
        return None

    def find_user_by_id_and_phone(self, user_id: str, phone_no: str, user_type: str) -> Union[UserEmployee, UserManager, None]:
        if user_type == "employee":
            return self.db.query(UserEmployee).filter(UserEmployee.id == user_id, UserEmployee.phone_no == phone_no).first()
        elif user_type == "manager":
            return self.db.query(UserManager).filter(UserManager.id == user_id, UserManager.phone_no == phone_no).first()
        return None


class UserService:
    def __init__(self, repository: UserRepository):
        self.repository = repository

    def forgot_id(self, name: str, email: str, user_type: str) -> Union[UserEmployee, UserManager, None]:
        return self.repository.find_user_by_name_and_email(name, email, user_type)

    def search_account(self, user_id: str, phone_no: str, user_type: str) -> Union[UserEmployee, UserManager, None]:
        return self.repository.find_user_by_id_and_phone(user_id, phone_no, user_type)

# 의존성 주입을 위한 함수


def get_user_service(db: Session = Depends(get_db)) -> UserService:
    repository = UserRepository(db)
    return UserService(repository)
