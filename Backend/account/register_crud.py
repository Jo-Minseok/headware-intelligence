from sqlalchemy.orm import Session
from account.account_schema import AccountInputCreate
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext

pwdContext = CryptContext(schemes=["bcrypt"], deprecated="auto")


class RegisterRepository:
    def __init__(self, db: Session):
        self.db = db

    def create_account(self, inputData: AccountInputCreate):
        if inputData.type == "employee":
            dbData = UserEmployee(
                id=inputData.id,
                password=pwdContext.hash(inputData.password),
                name=inputData.name,
                email=inputData.email,
                phoneNo=inputData.phoneNo,
                company=inputData.company
            )
        elif inputData.type == "manager":
            dbData = UserManager(
                id=inputData.id,
                password=pwdContext.hash(inputData.password),
                name=inputData.name,
                email=inputData.email,
                phoneNo=inputData.phoneNo,
                company=inputData.company
            )
        self.db.add(dbData)
        self.db.commit()

    def get_existing_account(self, inputData: AccountInputCreate):
        if inputData.type == "employee":
            return self.db.query(UserEmployee).filter(
                (UserEmployee.id == inputData.id) |
                (UserEmployee.email == inputData.email)
            ).first()
        elif inputData.type == "manager":
            return self.db.query(UserManager).filter(
                (UserManager.id == inputData.id) |
                (UserManager.email == inputData.email)
            ).first()
