from fastapi import APIRouter
import pymysql
router = APIRouter(prefix = "/register")

@router.post("/employee")
def post_employee_register():
    return 2

@router.post("/manager")
def post_manager_register():
    return 2
