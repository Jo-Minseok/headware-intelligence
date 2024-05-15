from db.models import Accident, AccidentProcessing, UserEmployee, Work_list
from sqlalchemy import select
from sqlalchemy.orm import Session
from db.db_connection import db_session
from common import SituationCode

# 사고 처리 데이터 조회
def get_accident_processing(db: Session, situationCode: str):
    return db.query(AccidentProcessing).filter(AccidentProcessing.situation != '오작동').all() if SituationCode[situationCode] != '오작동' else db.query(AccidentProcessing).filter(AccidentProcessing.situation == '오작동').all()

# 사고 데이터 조회(단일 데이터)
def get_accident(db: Session, no: int):
    return db.query(Accident).filter(Accident.no == no).first()

# 사고자 이름 조회
def get_victim_name(db: Session, no: int):
    victim_id = db.execute(select(Accident.victim_id).where(Accident.no == no)).fetchone()[0]
    victim_name = db.execute(select(UserEmployee.name).where(UserEmployee.employee_id == victim_id)).fetchone()[0]
    return victim_name
