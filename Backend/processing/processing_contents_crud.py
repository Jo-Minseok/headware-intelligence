from db.models import Accident, AccidentProcessing, UserEmployee
from sqlalchemy import select
from sqlalchemy.orm import Session

# 사고 처리 데이터 조회(처리 완료, 처리 중, 119 신고)
def get_accident_processing(db: Session):
    return db.query(AccidentProcessing).filter(AccidentProcessing.situation != '오작동').all()

# 사고 처리 데이터 조회(오작동)
def get_accident_processing_malfunction(db: Session):
    return db.query(AccidentProcessing).filter(AccidentProcessing.situation == '오작동').all()

# 사고 데이터 조회(단일 데이터)
def get_accident(db: Session, no: int):
    return db.query(Accident).filter(Accident.no == no).first()

# 사고자 이름 조회
def get_victim_name(db: Session, no: int):
    accident_query = select(Accident.victim_id).where(Accident.no == no)
    accident_result = db.execute(accident_query).fetchone()
    victim_id = accident_result[0]
    
    employee_query = select(UserEmployee.name).where(UserEmployee.employee_id == victim_id)
    employee_result = db.execute(employee_query).fetchone()
    victim_name = employee_result[0]
    
    return victim_name