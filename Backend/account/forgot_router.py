from fastapi import APIRouter, Depends, HTTPException, status
from account.forgot_crud import get_user_service
from account.forgot_crud import UserService
from account.account_schema import ForgotId, ForgotIdResult, ForgotPw

router = APIRouter(prefix='/forgot')


# 아이디 찾기
@router.post("/id", response_model=ForgotIdResult, status_code=status.HTTP_200_OK)
def get_employee_id(inputData: ForgotId, service: UserService = Depends(get_user_service)):
    searchResult = service.forgot_id(
        inputData.name, inputData.email, inputData.type)
    if not searchResult:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id': searchResult.id}

# 비밀번호 변경


@router.put("/pw", status_code=status.HTTP_200_OK)
def change_employee_pw(inputData: ForgotPw, service: UserService = Depends(get_user_service)):
    searchResult = service.search_account(
        inputData.id, inputData.phoneNo, inputData.type)
    if not searchResult:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    service.change_password(searchResult, inputData.password)
