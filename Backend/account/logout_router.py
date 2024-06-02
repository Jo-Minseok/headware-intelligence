from fastapi import APIRouter, Depends
from fastapi import APIRouter, status
from requests import Session
from db.db_connection import get_db
from fcm_notification import fcm_function

router = APIRouter()


@router.post("/logout", status_code=status.HTTP_200_OK)
def get_employee_login(id: str, alert_token: str, db: Session = Depends(get_db)):
    fcm_function.fcm_unsubscribe_all_topic(id, alert_token, db)
