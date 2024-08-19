from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from db.db_connection import get_db
from account.account_schema import AccountInputUpdate, FindAccount
from account.update_account_service import UpdateAccountService, UpdateAccountRepository

router = APIRouter(prefix="/account")


def get_update_account_service(db: Session = Depends(get_db)) -> UpdateAccountService:
    repository = UpdateAccountRepository(db)
    return UpdateAccountService(repository)


@router.post("/update", status_code=status.HTTP_200_OK)
def post_account_update(findKey: FindAccount, inputData: AccountInputUpdate, service: UpdateAccountService = Depends(get_update_account_service)):
    try:
        service.update_account(findKey, inputData)
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail=str(e))
