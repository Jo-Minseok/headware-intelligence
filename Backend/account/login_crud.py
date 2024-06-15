from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager
from typing import Union


class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_user_by_id(self, user_id: str, user_type: str) -> Union[UserEmployee, UserManager, None]:
        if user_type == "employee":
            return self.db.query(UserEmployee).filter(UserEmployee.id == user_id).first()
        elif user_type == "manager":
            return self.db.query(UserManager).filter(UserManager.id == user_id).first()
        return None
