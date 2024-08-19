from db.models import Accident, AccidentProcessing, UserEmployee, WorkList
from sqlalchemy import select
from sqlalchemy.orm import Session
from common import SituationCode

# 사고 처리 데이터 조회


def get_accident_processings(db: Session, manager: str, situationCode: str):
    res = []
    workId = db.execute(select(WorkList.workId).where(
        WorkList.managerId == manager)).all()
    if SituationCode[situationCode] == '오작동':
        for id in workId:
            accidentNo = db.execute(select(Accident.no).where(
                Accident.workId == id[0])).all()
            for no in accidentNo:
                accidentProcessing = db.query(AccidentProcessing).filter(
                    AccidentProcessing.no == no[0]).first()
                if accidentProcessing is not None and accidentProcessing.situation == '오작동':
                    res.append(accidentProcessing)
    else:
        for id in workId:
            accidentNo = db.execute(select(Accident.no).where(
                Accident.workId == id[0])).all()
            for no in accidentNo:
                accidentProcessing = db.query(AccidentProcessing).filter(
                    AccidentProcessing.no == no[0]).first()
                if accidentProcessing is not None and accidentProcessing.situation != '오작동':
                    res.append(accidentProcessing)
    return res

# 사고 데이터 조회(단일 데이터)


def get_accident(db: Session, no: int):
    return db.query(Accident).filter(Accident.no == no, Accident.victimId.isnot(None)).first()

# 사고자 이름 조회


def get_victim_name(db: Session, no: int):
    victimId = db.execute(select(Accident.victimId).where(
        Accident.no == no)).fetchone()[0]
    victimName = db.execute(select(UserEmployee.name).where(
        UserEmployee.id == victimId)).fetchone()[0]

    return victimName
