from db.models import Accident, AccidentProcessing, UserEmployee
from sqlalchemy import select
from sqlalchemy.orm import Session
from common import SituationCode

# 사고 처리 데이터 조회
def get_all_accident_processing(db: Session, situationCode: str):
    query = select(
        AccidentProcessing.no, 
        Accident.date, 
        Accident.time, 
        Accident.latitude, 
        Accident.longitude, 
        Accident.category, 
        UserEmployee.name, 
        AccidentProcessing.situation, 
        AccidentProcessing.date, 
        AccidentProcessing.time, 
        AccidentProcessing.detail
    ).where(
        AccidentProcessing.no == Accident.no and 
        AccidentProcessing.situation in SituationCode[situationCode] and 
        Accident.victim_id == UserEmployee.employee_id
    )
    return db.execute(query).all()
