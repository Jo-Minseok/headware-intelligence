from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager
from typing import Union
from db.db_connection import get_db
from fastapi import Depends


class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def find_user_by_name_and_email(self, name: str, email: str, userType: str) -> Union[UserEmployee, UserManager, None]:
        if userType == "employee":
            return self.db.query(UserEmployee).filter(UserEmployee.name == name, UserEmployee.email == email).first()
        elif userType == "manager":
            return self.db.query(UserManager).filter(UserManager.name == name, UserManager.email == email).first()
        return None

    def find_user_by_id_and_phone(self, userId: str, phoneNo: str, userType: str) -> Union[UserEmployee, UserManager, None]:
        if userType == "employee":
            return self.db.query(UserEmployee).filter(UserEmployee.id == userId, UserEmployee.phoneNo == phoneNo).first()
        elif userType == "manager":
            return self.db.query(UserManager).filter(UserManager.id == userId, UserManager.phoneNo == phoneNo).first()
        return None


class UserService:
    def __init__(self, repository: UserRepository):
        self.repository = repository

    def forgot_id(self, name: str, email: str, userType: str) -> Union[UserEmployee, UserManager, None]:
        return self.repository.find_user_by_name_and_email(name, email, userType)

    def search_account(self, userId: str, phoneNo: str, userType: str) -> Union[UserEmployee, UserManager, None]:
        return self.repository.find_user_by_id_and_phone(userId, phoneNo, userType)

# 의존성 주입을 위한 함수


def get_user_service(db: Session = Depends(get_db)) -> UserService:
    repository = UserRepository(db)
    return UserService(repository)
