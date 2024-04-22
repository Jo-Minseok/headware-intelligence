from fastapi import APIRouter
from typing import Tuple
from pydantic import BaseModel
router = APIRouter(prefix="/register")


@router.post("/employee/{account_items}}")
def post_employee_register(account_items: Tuple[str]):
    return 2


@ router.post("/manager/{account_items}")
def post_manager_register(account_items: Tuple[str]):
    return 2
