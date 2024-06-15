from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from db.db_connection import get_db
from account.account_schema import AccountInputCreate
from account.register_service import RegisterService, RegisterRepository

router = APIRouter(prefix="")


def get_register_service(db: Session = Depends(get_db)) -> RegisterService:
    repository = RegisterRepository(db)
    return RegisterService(repository)


@router.post("/register", status_code=status.HTTP_200_OK)
def post_account_register(inputData: AccountInputCreate, service: RegisterService = Depends(get_register_service)):
    try:
        service.create_account(inputData)
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail=str(e))
