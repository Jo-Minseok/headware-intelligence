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
from account.account_schema import LoginOutput
from db.db_connection import get_db


class SecureSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./account/.env', env_file_encoding='utf-8')
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    SECRET_KEY: str
    ALGORITHM: str


secureObject = SecureSettings(
    _env_file=r'./account/.env', _env_file_encoding='utf-8')

expire = datetime.utcnow() + timedelta(minutes=secureObject.ACCESS_TOKEN_EXPIRE_MINUTES)


class UserService:
    def __init__(self, repository: UserRepository):
        self.repository = repository
        self.pwdContext = CryptContext(schemes=["bcrypt"], deprecated="auto")

    def authenticate_user(self, username: str, password: str, userType: str) -> Union[UserEmployee, UserManager, None]:
        user = self.repository.get_user_by_id(username, userType)
        if user and self.pwdContext.verify(password, user.password):
            return user
        return None

    def create_access_token(self, data: dict) -> str:
        toEncode = data.copy()
        toEncode.update({"exp": expire})
        encodedJwt = jwt.encode(
            toEncode, secureObject.SECRET_KEY, algorithm=secureObject.ALGORITHM)
        return encodedJwt

    def update_user_tokens(self, user, accessToken: str, alertToken: str):
        user.loginToken = accessToken
        user.alertToken = alertToken
        self.repository.db.commit()

    def subscribe_manager_to_fcm(self, username: str, alertToken: str):
        fcm_function.fcm_subscribe_all_topic(
            username, alertToken, self.repository.db)


router = APIRouter()


def get_user_service(db: Session = Depends(get_db)) -> UserService:
    repository = UserRepository(db)
    return UserService(repository)


@router.post("/login", response_model=LoginOutput, status_code=status.HTTP_200_OK)
def get_employee_login(alertToken: str, type: str, accountData: OAuth2PasswordRequestForm = Depends(), service: UserService = Depends(get_user_service)):
    user = service.authenticate_user(
        accountData.username, accountData.password, type)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="아이디와 비밀번호가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"},
        )
    accessToken = service.create_access_token({"sub": user.id})
    service.update_user_tokens(user, accessToken, alertToken)

    if type == "manager":
        service.subscribe_manager_to_fcm(accountData.username, alertToken)

    return {
        "id": user.id,
        "name": user.name,
        "phoneNo": user.phoneNo,
        "email": user.email,
        "company": user.company,
        "accessToken": accessToken,
        "tokenType": "bearer",
    }
