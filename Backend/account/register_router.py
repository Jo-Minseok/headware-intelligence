from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from account import register_crud
from account.account_schema import Account_Input_Create
from db.db_connection import get_db
from starlette import status

router = APIRouter(prefix="")


@router.post("/register", status_code=status.HTTP_200_OK)
def post_account_register(input_data: Account_Input_Create, db: Session = Depends(get_db)):
    exist = register_crud.get_existing_account(input_data, db)
    if exist:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail="이미 존재하는 계정")
    register_crud.create_account(input_data, db)
