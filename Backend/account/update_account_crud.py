from sqlalchemy.orm import Session
from account.account_schema import AccountInputUpdate, FindAccount
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext

pwdContext = CryptContext(schemes=["bcrypt"], deprecated="auto")


class UpdateAccountRepository:
    def __init__(self, db: Session):
        self.db = db

    def update_account(self, findKey: FindAccount, inputData: AccountInputUpdate):
        if inputData.type == "employee":
            employee = self.db.query(UserEmployee).filter(
                UserEmployee.id == findKey.id).first()
            if not employee:
                raise ValueError("존재하지 않는 계정")
            employee.password = pwdContext.hash(inputData.password)
            employee.name = inputData.name
            employee.email = inputData.email
            employee.phoneNo = inputData.phoneNo
            employee.company = inputData.company
            self.db.add(employee)
        elif inputData.type == "manager":
            manager = self.db.query(UserManager).filter(
                UserManager.id == findKey.id).first()
            if not manager:
                raise ValueError("존재하지 않는 계정")
            manager.password = pwdContext.hash(inputData.password)
            manager.name = inputData.name
            manager.email = inputData.email
            manager.phoneNo = inputData.phoneNo
            manager.company = inputData.company
            self.db.add(manager)
        self.db.commit()
