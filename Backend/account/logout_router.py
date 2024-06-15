from fastapi import APIRouter, Depends
from fastapi import APIRouter, status
from requests import Session
from db.db_connection import get_db
from fcm_notification import fcm_function


class LogoutRepository:
    def __init__(self, db: Session):
        self.db = db

    def unsubscribe_all_topics(self, user_id: str, alert_token: str):
        fcm_function.fcm_unsubscribe_all_topic(user_id, alert_token, self.db)


class LogoutService:
    def __init__(self, repository: LogoutRepository):
        self.repository = repository

    def logout_user(self, user_id: str, alert_token: str):
        self.repository.unsubscribe_all_topics(user_id, alert_token)


router = APIRouter()


def get_logout_service(db: Session = Depends(get_db)) -> LogoutService:
    repository = LogoutRepository(db)
    return LogoutService(repository)


@router.post("/logout", status_code=status.HTTP_200_OK)
def logout_user(id: str, alertToken: str, service: LogoutService = Depends(get_logout_service)):
    service.logout_user(id, alertToken)
