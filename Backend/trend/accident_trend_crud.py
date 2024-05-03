from db.models import Accident
from sqlalchemy.orm import Session
from datetime import datetime

# 사고 발생 데이터를 날짜에 따라 조회
def get_accidents_by_date_range(db: Session, start_date: datetime, end_date: datetime):
    return db.query(Accident).filter(Accident.date.between(start_date, end_date)).all()