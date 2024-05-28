from db.models import Accident
from sqlalchemy.orm import Session

# 사고 발생 데이터 조회
def get_accidents(db: Session):
    return db.query(Accident).all()
