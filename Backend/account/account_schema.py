from typing import Optional
from pydantic import BaseModel, field_validator, EmailStr
from pydantic_core.core_schema import FieldValidationInfo

# 공통 검증 클래스


class Validators:
    @staticmethod
    def not_empty(v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v

    @staticmethod
    def passwords_match(v, info: FieldValidationInfo):
        if 'password' in info.data and v != info.data['password']:
            raise ValueError('비밀번호가 일치하지 않습니다')
        return v

# 로그인 반환, 입력 스키마


class LoginOutput(BaseModel):
    id: str
    name: str
    phoneNo: str
    email: EmailStr
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

    @field_validator('id', 'password', 'name', 'email', 'phoneNo', 'type', allow_reuse=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)

    @field_validator('rePassword', allow_reuse=True)
    def passwords_match(cls, v, info: FieldValidationInfo):
        return Validators.passwords_match(v, info)

# 아이디 찾기 스키마


class ForgotId(BaseModel):
    name: str
    email: EmailStr
    type: str

    @field_validator('name', 'email', 'type', allow_reuse=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)

# 아이디 찾기 결과 스키마


class ForgotIdResult(BaseModel):
    id: str

# 비밀번호 변경 스키마


class ForgotPw(BaseModel):
    id: str
    phoneNo: str
    password: str
    rePassword: str
    type: str

    @field_validator('password', 'rePassword', 'id', 'phoneNo', 'type', allow_reuse=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)

    @field_validator('rePassword', allow_reuse=True)
    def passwords_match(cls, v, info: FieldValidationInfo):
        return Validators.passwords_match(v, info)
