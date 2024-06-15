import firebase_admin.messaging
from db.models import WorkList, UserEmployee
from sqlalchemy.orm import Session
# 알림 객체 생성
import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate("./fcm_notification/firebase-privatekey.json")
firebase_admin.initialize_app(cred)


def fcm_subscribe_all_topic(managerId: str, alertToken: str, db: Session):
    workListRows = db.query(WorkList.workId).filter(
        WorkList.managerId == managerId).all()
    for workRow in workListRows:
        # workRow는 튜플이 아니라 WorkList의 workId 속성을 가진 객체
        workId = workRow.workId
        firebase_admin.messaging.subscribe_to_topic(alertToken, workId)


def fcm_unsubscribe_all_topic(managerId: str, alertToken: str, db: Session):
    workListRows = db.query(WorkList.workId).filter(
        WorkList.managerId == managerId).all()
    for workRow in workListRows:
        # workRow는 튜플이 아니라 WorkList의 workId 속성을 가진 객체
        workId = workRow.workId
        firebase_admin.messaging.unsubscribe_from_topic(alertToken, workId)


def fcm_subscribe_one_topic(alertToken: str, workId: str):
    firebase_admin.messaging.subscribe_to_topic(alertToken, workId)


def fcm_unsubscribe_one_topic(alertToken: str, workId: str):
    firebase_admin.messaging.unsubscribe_from_topic(alertToken, workId)


def fcm_send_messaging(workId: str, victim_id: str, db: Session):
    victim_name = db.query(UserEmployee.name).filter(
        UserEmployee.id == victim_id).scalar()
    work_name = db.query(WorkList.name).filter(
        WorkList.workId == workId).scalar()
    message = firebase_admin.messaging.Message(
        notification=firebase_admin.messaging.Notification(
            title="사고 발생!",
            body=work_name + "에서 " + victim_name + "("+victim_id+")님께서 사고가 발생했습니다!"),
        topic=workId
    )
    firebase_admin.messaging.send(message)


def fcm_send_emergency(workId: str, user_id: str, db: Session):
    victim_name = db.query(UserEmployee.name).filter(
        UserEmployee.id == user_id).scalar()
    work_name = db.query(WorkList.name).filter(
        WorkList.workId == workId).scalar()
    message = firebase_admin.messaging.Message(
        notification=firebase_admin.messaging.Notification(
            title="긴급 호출 발생!",
            body=work_name + "에서 " + victim_name + "(" + user_id + ")님께서 위기 호출을 하셨습니다!"),
        topic=workId
    )
    firebase_admin.messaging.send(message)
