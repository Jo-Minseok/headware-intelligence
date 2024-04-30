from pydantic import BaseModel, field_validator, EmailStr
from pydantic_core.core_schema import FieldValidationInfo


# 근로자 로그인 스키마
class Employee_Login(BaseModel):
    id: str
    access_token: str
    token_type: str


# 근로자 계정 생성 스키마
class Employee_Create(BaseModel):
    id: str
    password: str
    re_password: str
    name: str
    email: EmailStr
    manager: str
    phone_no: str
    company: str

    @field_validator('id', 'password', 'name', 'email', 'manager', 'phone_no', 'company')
    def not_empty(cls, v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v


# 관리자 로그인 스키마
class Manager_Login(BaseModel):
    id: str
    access_token: str
    token_type: str


# 관리자 계정 생성 스키마
class Manager_Create(BaseModel):
    id: str
    password: str
    re_password: str
    name: str
    email: EmailStr
    company: str