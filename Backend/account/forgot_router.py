from fastapi import APIRouter, Depends, HTTPException, status
from db.db_connection import get_db
from account import account_schema
from sqlalchemy.orm import Session
from account import forgot_crud

router = APIRouter(prefix="/forgot")

@router.post("/employee/id", id=account_schema.Employee_Forgot_Id_Result)
def get_employee_id(user_employee: account_schema.Employee_Forgot_Id, db: Session = Depends(get_db)):
    employee = forgot_crud.get_employee_findId(db, user_employee.name, user_employee.email)
    if not employee:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id' : employee.id}

# 수정 필요
@router.post("/employee/pw", status_code=status.HTTP_204_NO_CONTENT)
def update_employee_pw(user_employee: account_schema.Employee_Forgot_Pw, db: Session = Depends(get_db)):
    employee = forgot_crud.get_employee_findPw(db, user_employee.id, user_employee.email)
    if not employee:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    

@router.post("/manager/id", id=account_schema.Manager_Forgot_Id_Result)
def get_manager_id(user_manager: account_schema.Employee_Forgot_Id, db: Session = Depends(get_db)):
    manager = forgot_crud.get_manager_findId(db, user_manager.name, user_manager.email)
    if not manager:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id' : manager.id}

# 수정 필요
@router.post("/manager/pw", status_code=status.HTTP_204_NO_CONTENT)
def update_manager_pw(user_manager: account_schema.Manager_Forgot_Pw, db: Session = Depends(get_db)):
    manager = forgot_crud.get_manager_findPw(db, user_manager.id, user_manager.email)
    if not manager:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    