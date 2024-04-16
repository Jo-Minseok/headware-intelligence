from fastapi import APIRouter
import pymysql
router = APIRouter()


@router.post("/register")
def post_register():
    return 2
