from typing import Optional
from pydantic import BaseModel, field_validator, EmailStr
from pydantic_core.core_schema import FieldValidationInfo


# 로그인 반환, 입력 스키마
class Login_Output(BaseModel):
    id: str
    name: str
    access_token: str
    token_type: str

# 계정 생성 스키마
class Account_Input_Create(BaseModel):
    id: str
    password: str
    re_password: str
    name: str
    email: EmailStr
    phone_no: str
    company: Optional[str] = None
    type: str

    @field_validator('id', 'password', 'name', 'email', 'phone_no', 'type')
    def not_empty(cls, v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v

    @field_validator('re_password')
    def passwords_match(cls, v, info: FieldValidationInfo):
        if 'password' in info.data and v != info.data['password']:
            raise ValueError('비밀번호가 일치하지 않습니다')
        return v


# 아이디 찾기 스키마
class Forgot_Id(BaseModel):
    name: str
    email: EmailStr
    type: str

    @field_validator('name', 'email', 'type')
    def not_empty(cls, v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v


# 아이디 찾기 결과 스키마
class Forgot_Id_Result(BaseModel):
    id: str


# 비밀번호 변경 스키마
class Forgot_Pw(BaseModel):
    id: str
    phone_no: str
    password: str
    re_password: str
    type: str

    @field_validator('password', 're_password', 'id', 'phone_no', 'type')
    def not_empty(cls, v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v

    @field_validator('re_password')
    def passwords_match(cls, v, info: FieldValidationInfo):
        if 'password' in info.data and v != info.data['password']:
            raise ValueError('비밀번호가 일치하지 않습니다')
        return v
