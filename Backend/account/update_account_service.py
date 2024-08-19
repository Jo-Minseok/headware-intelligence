from .update_account_crud import UpdateAccountRepository
from account.account_schema import AccountInputUpdate, FindAccount


class UpdateAccountService:
    def __init__(self, repository: UpdateAccountRepository):
        self.repository = repository

    def update_account(self, findKey: FindAccount, inputData: AccountInputUpdate):
        self.repository.update_account(findKey, inputData)
