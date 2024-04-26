from fastapi import APIRouter, Depends, HTTPException, status
from datetime import timedelta, datetime
from db.db_connection import get_db
from account.account_schema import Employee_Login
from db import db_connection
from sqlalchemy.orm import Session
from account import login_crud
from passlib.context import CryptContext
from fastapi.security import OAuth2PasswordRequestForm
from account.register_crud import pwd_context
from pydantic_settings import BaseSettings, SettingsConfigDict
from jose import jwt


class SecureSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./account/.env', env_file_encoding='utf-8')
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    SECRET_KEY: str
    ALGORITHM: str


secure_object = SecureSettings(
    _env_file=r'./account/.env', _env_file_encoding='utf-8')

router = APIRouter(prefix="/login")


@router.post("/employee", response_model=Employee_Login)
def get_employee_login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user_row = login_crud.get_employee(db, form_data.username)
    if not user_row or not pwd_context.verify(form_data.password, user_row.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="아이디와 비밀번호가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"}
        )
    data = {
        "sub": user_row.id,
        "exp": datetime.utcnow() + timedelta(minutes=secure_object.ACCESS_TOKEN_EXPIRE_MINUTES)
    }
    access_token = jwt.encode(
        data, secure_object.SECRET_KEY, algorithm=secure_object.ALGORITHM)
    return {
        "id": user_row.id,
        "access_token": access_token,
        "token_type": "bearer"
    }


@router.post("/manager")
def get_manager_login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user_row = login_crud.get_manager(db, form_data.username)
    if not user_row or not pwd_context.verify(form_data.password, user_row.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="아이디와 비밀번호가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"}
        )
    data = {
        "sub": user_row.id,
        "exp": datetime.utcnow() + timedelta(minutes=secure_object.ACCESS_TOKEN_EXPIRE_MINUTES)
    }
    access_token = jwt.encode(
        data, secure_object.SECRET_KEY, algorithm=secure_object.ALGORITHM)
    return {
        "id": user_row.id,
        "access_token": access_token,
        "token_type": "bearer"
    }
