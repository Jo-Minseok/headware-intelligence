from fastapi import APIRouter
import oracledb

router = APIRouter()


@router.post("/register")
def post_register():
    con = oracledb.connect(user='USERNAME', password='PW', dns='IP')
    cursor = con.cursor()
    con.close()
    return 2
