from pydantic_settings import BaseSettings, SettingsConfigDict
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from datetime import datetime, timedelta
from typing import Union
from jose import jwt
from db.models import UserEmployee, UserManager
from fcm_notification import fcm_function
from account.login_crud import UserRepository
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from account.account_schema import Login_Output
from db.db_connection import get_db


class SecureSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file='./account/.env', env_file_encoding='utf-8')
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    SECRET_KEY: str
    ALGORITHM: str


secure_object = SecureSettings(
    _env_file=r'./account./env', _env_file_encoding='utf-8')


class UserService:
    def __init__(self, repository: UserRepository):
        self.repository = repository
        self.pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

    def authenticate_user(self, username: str, password: str, user_type: str) -> Union[UserEmployee, UserManager, None]:
        user = self.repository.get_user_by_id(username, user_type)
        if user and self.pwd_context.verify(password, user.password):
            return user
        return None

    def create_access_token(self, data: dict) -> str:
        to_encode = data.copy()
        expire = datetime.utcnow() + timedelta(minutes=secure_object.ACCESS_TOKEN_EXPIRE_MINUTES)
        to_encode.update({"exp": expire})
        encoded_jwt = jwt.encode(
            to_encode, secure_object.SECRET_KEY, algorithm=secure_object.ALGORITHM)
        return encoded_jwt

    def update_user_tokens(self, user, access_token: str, alert_token: str):
        user.login_token = access_token
        user.alert_token = alert_token
        self.repository.db.commit()

    def subscribe_manager_to_fcm(self, username: str, alert_token: str):
        fcm_function.fcm_subscribe_all_topic(
            username, alert_token, self.repository.db)


router = APIRouter()


def get_user_service(db: Session = Depends(get_db)) -> UserService:
    repository = UserRepository(db)
    return UserService(repository)


@router.post("/login", response_model=Login_Output, status_code=status.HTTP_200_OK)
def get_employee_login(alert_token: str, type: str, account_data: OAuth2PasswordRequestForm = Depends(), service: UserService = Depends(get_user_service)):
    user = service.authenticate_user(
        account_data.username, account_data.password, type)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="아이디와 비밀번호가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token = service.create_access_token({"sub": user.id})
    service.update_user_tokens(user, access_token, alert_token)

    if type == "manager":
        service.subscribe_manager_to_fcm(account_data.username, alert_token)

    return {
        "id": user.id,
        "name": user.name,
        "phoneNo": user.phone_no,
        "email": user.email,
        "access_token": access_token,
        "token_type": "bearer",
    }
