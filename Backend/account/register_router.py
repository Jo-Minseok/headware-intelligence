from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from account import account_schema, register_crud
from db.db_connection import get_db
from starlette import status


# 라우터 생성 전위 URL은 register
router = APIRouter(prefix="/register")


"""
근로자 회원가입 라우터, 코드가 없을 경우 204 NO_CONTENT 예외 발생
get_existing_employee 함수를 이용하여 아이디 있는 지 확인. 존재시, HTTP 예외 409 발생.
없을 경우, create_employee 함수 실행하여 데이터 삽입
"""


@router.post("/employee", status_code=status.HTTP_204_NO_CONTENT)
def post_employee_register(_user_employee: account_schema.Employee_Create, db: Session = Depends(get_db)):
    exist = register_crud.get_existing_employee(
        db, user_employee=_user_employee)
    if exist:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail="이미 존재하는 계정")
    register_crud.create_employee(db=db, user_employee=_user_employee)


"""
관리자 회원가입 라우터, 코드가 없을 경우 204 NO_CONTENT 예외 발생
get_existing_employee 함수를 이용하여 아이디 있는 지 확인. 존재시, HTTP 예외 409 발생.
없을 경우, create_manager 함수 실행하여 데이터 삽입
"""


@router.post("/manager", status_code=status.HTTP_204_NO_CONTENT)
def post_manager_register(_user_manager: account_schema.Manager_Create, db: Session = Depends(get_db)):
    exist = register_crud.get_existing_manager(
        db, user_manager=_user_manager)
    if exist:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail="이미 존재하는 계정")
    register_crud.create_manager(db=db, user_manager=_user_manager)
