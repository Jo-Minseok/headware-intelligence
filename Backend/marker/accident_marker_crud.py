from db.models import Accident, AccidentProcessing, UserEmployee
from sqlalchemy import select
from sqlalchemy.orm import Session
from marker.accident_marker_schema import Accident_Processing_Detail
from common import SituationCode
import datetime

# 사고 데이터 조회(모든 데이터)
def get_all_accident(db: Session):
    return db.query(Accident).all()

# 사고 처리 데이터 조회(모든 데이터)
def get_all_accident_processing(db: Session):
    return db.query(AccidentProcessing).all()

# 사고 데이터 조회(단일 데이터)
def get_accident(db: Session, no: int):
    return db.query(Accident).filter(Accident.no == no).first()

# 사고 처리 데이터 조회(단일 데이터)
def get_accident_processing(db: Session, no: int):
    return db.query(AccidentProcessing).filter(AccidentProcessing.no == no).first()

# 사고자 이름 조회
def get_victim_name(db: Session, no: int):
    victim_id = db.execute(select(Accident.victim_id).where(Accident.no == no)).fetchone()[0]
    victim_name = db.execute(select(UserEmployee.name).where(UserEmployee.id == victim_id)).fetchone()[0]
    return victim_name

# 사고 처리 데이터 갱신
def update_accident_processing(db: Session, no: int, situationCode: str, detail: Accident_Processing_Detail):
    accident = db.query(AccidentProcessing).filter(AccidentProcessing.no == no).first()
    accident.situation = SituationCode[situationCode]
    accident.detail = detail.detail
    accident.date = datetime.date.today().strftime('%Y-%m-%d')
    accident.time = datetime.datetime.now().strftime('%H:%M:%S')
    db.add(accident)
    db.commit()
