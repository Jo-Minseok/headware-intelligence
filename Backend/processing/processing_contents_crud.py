from db.models import Accident, AccidentProcessing, UserEmployee, Work_list
from sqlalchemy import select
from sqlalchemy.orm import Session
from common import SituationCode

# 사고 처리 데이터 조회
def get_accident_processings(db: Session, manager: str, situationCode: str):
    res = []
    work_id = db.execute(select(Work_list.work_id).where(Work_list.manager == manager)).all()
    if SituationCode[situationCode] == '오작동':
        for id in work_id:
            accident_no = db.execute(select(Accident.no).where(Accident.work_id == id[0])).all()
            for no in accident_no:
                accident_processing = db.query(AccidentProcessing).filter(AccidentProcessing.no == no[0]).first()
                if accident_processing is not None and accident_processing.situation == '오작동':
                    res.append(accident_processing)
    else:
        for id in work_id:
            accident_no = db.execute(select(Accident.no).where(Accident.work_id == id[0])).all()
            for no in accident_no:
                accident_processing = db.query(AccidentProcessing).filter(AccidentProcessing.no == no[0]).first()
                if accident_processing is not None and accident_processing.situation != '오작동':
                    res.append(accident_processing)
    return res

# 사고 데이터 조회(단일 데이터)
def get_accident(db: Session, no: int):
    return db.query(Accident).filter(Accident.no == no).first()

# 사고자 이름 조회
def get_victim_name(db: Session, no: int):
    victim_id = db.execute(select(Accident.victim_id).where(Accident.no == no)).fetchone()[0]
    victim_name = db.execute(select(UserEmployee.name).where(UserEmployee.id == victim_id)).fetchone()[0]
    return victim_name
