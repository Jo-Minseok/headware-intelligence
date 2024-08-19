from typing import Optional
from pydantic import BaseModel, EmailStr, validator

# 공통 검증 클래스


class Validators:
    @staticmethod
    def not_empty(v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v

    @staticmethod
    def passwords_match(v, info):
        if 'password' in info and v != info['password']:
            raise ValueError('비밀번호가 일치하지 않습니다.')
        return v

# 로그인 반환, 입력 스키마


class LoginOutput(BaseModel):
    id: str
    name: str
    phoneNo: str
    email: EmailStr
    company: str
    accessToken: str
    tokenType: str

# 계정 생성 스키마


class AccountInputCreate(BaseModel):
    id: str
    password: str
    rePassword: str
    name: str
    email: EmailStr
    phoneNo: str
    company: Optional[str] = None
    type: str

    @validator('id', 'password', 'name', 'email', 'phoneNo', 'type', pre=True, always=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)

    @validator('rePassword')
    def passwords_match(cls, v, values, **kwargs):
        return Validators.passwords_match(v, values)

# 아이디 찾기 스키마


class ForgotId(BaseModel):
    name: str
    email: EmailStr
    type: str

    @validator('name', 'email', 'type', pre=True, always=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)

# 비밀번호 변경 스키마


class ForgotPw(BaseModel):
    id: str
    phoneNo: str
    password: str
    rePassword: str
    type: str

    @validator('id', 'phoneNo', 'password', 'type', pre=True, always=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)

    @validator('rePassword')
    def passwords_match(cls, v, values, **kwargs):
        return Validators.passwords_match(v, values)


class ForgotIdResult(BaseModel):
    id: str
