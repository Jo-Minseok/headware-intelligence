from sqlalchemy.orm import Session
from db.models import UserEmployee, UserManager
from typing import Union


class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_user_by_id(self, userId: str, userType: str) -> Union[UserEmployee, UserManager, None]:
        if userType == "employee":
            return self.db.query(UserEmployee).filter(UserEmployee.id == userId).first()
        elif userType == "manager":
            return self.db.query(UserManager).filter(UserManager.id == userId).first()
        return None
