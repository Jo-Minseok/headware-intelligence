from .register_crud import RegisterRepository
from account.account_schema import Account_Input_Create


class RegisterService:
    def __init__(self, repository: RegisterRepository):
        self.repository = repository

    def create_account(self, input_data: Account_Input_Create):
        if self.repository.get_existing_account(input_data):
            raise ValueError("이미 존재하는 계정")
        self.repository.create_account(input_data)
