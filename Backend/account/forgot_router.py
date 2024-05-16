from fastapi import APIRouter, Depends, HTTPException, status
from db.db_connection import get_db
from account.account_schema import Forgot_Id, Forgot_Id_Result, Forgot_Pw
from sqlalchemy.orm import Session
from account import forgot_crud
from passlib.context import CryptContext

router = APIRouter(prefix='/forgot')


# 아이디 찾기
@router.post("/id", response_model=Forgot_Id_Result)
def get_employee_id(input_data: Forgot_Id, db: Session = Depends(get_db)):
    search_result = forgot_crud.forgot_id(
        input_data.name, input_data.email, input_data.type, db)
    if not search_result:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id': search_result.id}


# 비밀번호 변경
@router.put("/pw", status_code=status.HTTP_200_OK)
def change_employee_pw(input_data: Forgot_Pw, db: Session = Depends(get_db)):
    pwd_context = CryptContext(schemes=['bcrypt'], deprecated='auto')
    search_result = forgot_crud.search_account(
        input_data.id, input_data.phone_no, input_data.type, db)
    if not search_result:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    search_result.password = pwd_context.hash(input_data.password)
    db.add(search_result)
    db.commit()
