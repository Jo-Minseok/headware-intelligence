from sqlalchemy.orm import Session
from account.account_schema import Account_Input_Create
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


class RegisterRepository:
    def __init__(self, db: Session):
        self.db = db

    def create_account(self, input_data: Account_Input_Create):
        if input_data.type == "employee":
            db_data = UserEmployee(
                id=input_data.id,
                password=pwd_context.hash(input_data.password),
                name=input_data.name,
                email=input_data.email,
                phone_no=input_data.phoneNo,
                company=input_data.company
            )
        elif input_data.type == "manager":
            db_data = UserManager(
                id=input_data.id,
                password=pwd_context.hash(input_data.password),
                name=input_data.name,
                email=input_data.email,
                phone_no=input_data.phoneNo,
                company=input_data.company
            )
        self.db.add(db_data)
        self.db.commit()

    def get_existing_account(self, input_data: Account_Input_Create):
        if input_data.type == "employee":
            return self.db.query(UserEmployee).filter(
                (UserEmployee.id == input_data.id) |
                (UserEmployee.email == input_data.email)
            ).first()
        elif input_data.type == "manager":
            return self.db.query(UserManager).filter(
                (UserManager.id == input_data.id) |
                (UserManager.email == input_data.email)
            ).first()
