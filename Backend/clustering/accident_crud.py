import sys, os
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from db.models import Accident
from db.db_connection import get_db
from sqlalchemy.orm import Session
from fastapi import Depends
from datetime import datetime
from db.models import Accident

# 사고 발생 데이터 불러오기
def get_accident(db: Session = Depends(get_db)):
    return db.query(Accident).all()

# 사고 발생 데이터 삽입(테스트 버전)
def insert_accident(db: Session = Depends(get_db)):
    # 테스트 데이터
    latitude = [37.5585, 40.7128, 51.5074, 48.8566, 35.6895, 52.5200, 55.7558, 41.8781, 45.4215, 34.0522, 31.2304, 48.8566, 38.9072, 37.7749, 55.6761, 53.3498, 49.2827, 36.7783]
    longitude = [126.9368, -74.0060, -0.1278, 2.3522, 139.6917, 13.4050, 37.6176, -87.6298, -75.6972, -118.2437, 121.4737, 2.3522, -77.0369, -122.4194, 12.5683, -6.2603, -123.1207, -119.4179]

    for lat, lon in zip(latitude, longitude):
        accident = Accident(date=datetime.today().strftime('%Y-%m-%d'), 
                            time=datetime.now().strftime('%H:%M:%S'), 
                            latitude=lat, 
                            longitude=lon, 
                            victim_id='user2', 
                            category='test')
        db.add(accident)

    db.commit()
    db.close()