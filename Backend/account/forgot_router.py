from fastapi import APIRouter, Depends, HTTPException, status
from account.forgot_crud import get_user_service
from account.forgot_crud import UserService
from account.account_schema import Forgot_Id, Forgot_Id_Result, Forgot_Pw

router = APIRouter(prefix='/forgot')


# 아이디 찾기
@router.post("/id", response_model=Forgot_Id_Result, status_code=status.HTTP_200_OK)
def get_employee_id(input_data: Forgot_Id, service: UserService = Depends(get_user_service)):
    search_result = service.forgot_id(
        input_data.name, input_data.email, input_data.type)
    if not search_result:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    return {'id': search_result.id}

# 비밀번호 변경


@router.put("/pw", status_code=status.HTTP_200_OK)
def change_employee_pw(input_data: Forgot_Pw, service: UserService = Depends(get_user_service)):
    search_result = service.search_account(
        input_data.id, input_data.phoneNo, input_data.type)
    if not search_result:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="존재하지 않는 계정")
    service.change_password(search_result, input_data.password)
