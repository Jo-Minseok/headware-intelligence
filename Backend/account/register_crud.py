from sqlalchemy.orm import Session
from account.account_schema import Employee_Create, Manager_Create
from db.models import UserEmployee, UserManager
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def create_employee(db: Session, user_employee: Employee_Create):
    db_employee = UserEmployee(id=user_employee.id,
                               password=pwd_context.hash(
                                   user_employee.password),
                               name=user_employee.name,
                               email=user_employee.email,
                               manager=user_employee.manager,
                               phone_no=user_employee.phone_no,
                               company=user_employee.company)
    db.add(db_employee)
    db.commit()


def get_existing_employee(db: Session, user_employee: Employee_Create):
    return db.query(UserEmployee).filter(
        (UserEmployee.id == user_employee.id) | (
            UserEmployee.email == user_employee.email)
    ).first()


def create_manager(db: Session, user_manager: Manager_Create):
    db_manager = UserManager(id=user_manager.id,
                             password=pwd_context.hash(user_manager.password),
                             name=user_manager.name,
                             email=user_manager.email,
                             company=user_manager.company)
    db.add(db_manager)
    db.commit()


def get_existing_manager(db: Session, user_manager: Manager_Create):
    return db.query(UserManager).filter(
        (UserManager.id == user_manager.id) | (
            UserManager.email == user_manager.email)
    ).first()
