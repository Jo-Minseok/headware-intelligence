from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from account import account_schema, register_crud
from db.db_connection import get_db
from starlette import status

router = APIRouter(prefix="/register")


@router.post("/employee", status_code=status.HTTP_204_NO_CONTENT)
def post_employee_register(_user_employee: account_schema.Employee_Create, db: Session = Depends(get_db)):
    exist = register_crud.get_existing_employee(
        db, user_employee=_user_employee)
    if exist:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail="이미 존재하는 계정")
    register_crud.create_employee(db=db, user_employee=_user_employee)


@router.post("/manager", status_code=status.HTTP_204_NO_CONTENT)
def post_manager_register(_user_manager: account_schema.Manager_Create, db: Session = Depends(get_db)):
    exist = register_crud.get_existing_manager(
        db, user_manager=_user_manager)
    if exist:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail="이미 존재하는 계정")
    register_crud.create_manager(db=db, user_manager=_user_manager)
