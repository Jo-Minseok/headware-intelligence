import firebase_admin.messaging
from db.models import Work_list, UserEmployee
from fastapi import Depends
from sqlalchemy.orm import Session
from db.db_connection import get_db
# 알림 객체 생성
import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate("./fcm_notification/firebase-privatekey.json")
firebase_admin.initialize_app(cred)


def fcm_subscribe_all_topic(manager_id: str, alert_token: str, db: Session):
    work_list_rows = db.query(Work_list.work_id).filter(
        Work_list.manager == manager_id)
    work_list_ids = [work_row for work_row in work_list_rows]
    for work_id in work_list_ids:
        firebase_admin.messaging.subscribe_to_topic(alert_token, work_id)


def fcm_unsubscribe_all_topic(manager_id: str, alert_token: str, db: Session):
    work_list_rows = db.query(Work_list.work_id).filter(
        Work_list.manager == manager_id)
    work_list_ids = [work_row for work_row in work_list_rows]
    for work_id in work_list_ids:
        firebase_admin.messaging.unsubscribe_from_topic(alert_token, work_id)


def fcm_subscribe_one_topic(alert_token: str, work_id: str):
    firebase_admin.messaging.subscribe_to_topic(alert_token, work_id)


def fcm_unsubscribe_one_topic(alert_token: str, work_id: str):
    firebase_admin.messaging.unsubscribe_from_topic(alert_token, work_id)


def fcm_send_messaging(work_id: str, victim_id: str, db: Session):
    victim_name = db.query(UserEmployee.name).filter(
        UserEmployee.id == victim_id)
    work_name = db.query(Work_list.name).filter(Work_list.work_id == work_id)
    message = firebase_admin.messaging.Message(
        notification=firebase_admin.messaging.Notification(
            title="사고 발생 삐용삐용",
            body=work_name + "에서 " + victim_name + "("+victim_id+")"+"님께서 사고가 발생했습니다!"),
        topic=work_id
    )
    response = firebase_admin.messaging.send(message)
