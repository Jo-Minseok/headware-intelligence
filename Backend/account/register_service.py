from .register_crud import RegisterRepository
from account.account_schema import AccountInputCreate


class RegisterService:
    def __init__(self, repository: RegisterRepository):
        self.repository = repository

    def create_account(self, inputData: AccountInputCreate):
        if self.repository.get_existing_account(inputData):
            raise ValueError("이미 존재하는 계정")
        self.repository.create_account(inputData)
