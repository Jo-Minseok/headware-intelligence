from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.responses import RedirectResponse
from db.db_connection import get_db
from account import account_schema
from sqlalchemy.orm import Session
from account import forgot_crud

router = APIRouter(prefix="/forgot")

@router.post("/employee/id", response_model=account_schema.Employee_Forgot_Id_Result)
def get_employee_id(user_employee: account_schema.Employee_Forgot_Id, db: Session = Depends(get_db)):
    employee = forgot_crud.forgot_employee_id(db, user_employee.name, user_employee.email)
    if not employee:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id' : employee.id}

@router.post("/employee/pw", status_code=status.HTTP_204_NO_CONTENT)
def confirm_employee(user_employee: account_schema.Employee_Forgot_Pw, db: Session = Depends(get_db)):
    employee = forgot_crud.forgot_employee_pw(db, user_employee.id, user_employee.email)
    if not employee:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return RedirectResponse(url='/forgot/employee/pw/change')

@router.post("/employee/pw/change", status_code=status.HTTP_204_NO_CONTENT)
def change_employee_pw(user_employee: account_schema.Employee_Forgot_Pw, pw_change: account_schema.Employee_Change_Pw, db: Session = Depends(get_db)):
    forgot_crud.update_employee_pw(db, user_employee.id, pw_change.password)

@router.post("/manager/id", response_model=account_schema.Manager_Forgot_Id_Result)
def get_manager_id(user_manager: account_schema.Manager_Forgot_Id, db: Session = Depends(get_db)):
    manager = forgot_crud.forgot_manager_id(db, user_manager.name, user_manager.email)
    if not manager:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id' : manager.id}

@router.post("/manager/pw", status_code=status.HTTP_204_NO_CONTENT)
def confirm_manager(user_manager: account_schema.Manager_Forgot_Pw, db: Session = Depends(get_db)):
    manager = forgot_crud.forgot_manager_pw(db, user_manager.id, user_manager.email)
    if not manager:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return RedirectResponse(url='/forgot/manager/pw/change')

@router.post("/manager/pw/change", status_code=status.HTTP_204_NO_CONTENT)
def change_manager_pw(user_manager: account_schema.Manager_Forgot_Pw, pw_change: account_schema.Manager_Change_Pw, db: Session = Depends(get_db)):
    forgot_crud.update_manager_pw(db, user_manager.id, pw_change.password)
    