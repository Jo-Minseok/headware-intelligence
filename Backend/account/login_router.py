from fastapi import APIRouter, Depends, HTTPException, status
from datetime import timedelta, datetime
from db.db_connection import get_db
from account.account_schema import Employee_Login, Manager_Login
from sqlalchemy.orm import Session
from account import login_crud
from fastapi.security import OAuth2PasswordRequestForm
from account.register_crud import pwd_context  # 회원가입에서 사용했던 암호화 방식 이용
from pydantic_settings import BaseSettings, SettingsConfigDict
from jose import jwt


# 토큰 만료 시간, 암호화 키 Github 올리지 않기 위한 클래스 .env Load용
class SecureSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./account/.env', env_file_encoding='utf-8')
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    SECRET_KEY: str
    ALGORITHM: str


# .env Load 객체
secure_object = SecureSettings(
    _env_file=r'./account/.env', _env_file_encoding='utf-8')


# 라우터 생성
router = APIRouter(prefix="/login")


# employee 라우터 연결, 입력 모델은 OAuth2PasswordRequestForm + DB, 반환 모델은 Employee_Login 스키마
@router.post("/employee", response_model=Employee_Login)
def get_employee_login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    # OAuth2PasswordRequestForm에서 입력받은 ID가 존재하는 지 확인
    user_row = login_crud.get_employee(db, form_data.username)
    # ID가 없거나 비밀번호를 확인했을 때 잘 못 됐다면 HTTP 예외 발생
    if not user_row or not pwd_context.verify(form_data.password, user_row.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="아이디와 비밀번호가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"}
        )
    # jwt에서 이용할 데이터 저장. 사용자 ID, 토큰 만료 시간
    data = {
        "sub": user_row.id,
        "exp": datetime.utcnow() + timedelta(minutes=secure_object.ACCESS_TOKEN_EXPIRE_MINUTES)
    }
    # 토큰은 현재 시간에서 보안 토큰 만료 시간까지, 알고리즘은 비공개
    access_token = jwt.encode(
        data, secure_object.SECRET_KEY, algorithm=secure_object.ALGORITHM)
    user_row.login_token = access_token
    db.commit()
    # Employee_Login 스키마 반환
    return {
        "id": user_row.id,
        "access_token": access_token,
        "token_type": "bearer"
    }


# manager 라우터 연결, 입력 모델은 OAuth2PasswordRequestForm + DB, 반환 모델은 Manager_Login 스키마
@router.post("/manager", response_model=Manager_Login)
def get_manager_login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    # OAuth2PasswordRequestForm에서 입력받은 ID가 존재하는 지 확인
    user_row = login_crud.get_manager(db, form_data.username)
    # ID가 없거나 비밀번호를 확인했을 때 잘 못 됐다면 HTTP 예외 발생
    if not user_row or not pwd_context.verify(form_data.password, user_row.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="아이디와 비밀번호가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"}
        )
        # jwt에서 이용할 데이터 저장. 사용자 ID, 토큰 만료 시간
    data = {
        "sub": user_row.id,
        "exp": datetime.utcnow() + timedelta(minutes=secure_object.ACCESS_TOKEN_EXPIRE_MINUTES)
    }
    # 토큰은 현재 시간에서 보안 토큰 만료 시간까지, 알고리즘은 비공개
    access_token = jwt.encode(
        data, secure_object.SECRET_KEY, algorithm=secure_object.ALGORITHM)
    user_row.login_token = access_token
    db.commit()
    # Manager_Login 스키마 반환
    return {
        "id": user_row.id,
        "access_token": access_token,
        "token_type": "bearer"
    }
