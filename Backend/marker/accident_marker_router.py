from fastapi import APIRouter, Depends
from db.db_connection import get_db
from sqlalchemy.orm import Session
from marker import accident_marker_crud

router = APIRouter(prefix="/map")
    
@router.get("/marker")
def cluster_data(db: Session = Depends(get_db)):
    # 사고 발생 데이터 조회
    accidents = accident_marker_crud.get_accidents(db=db)
    
    # 지도 위도, 경도 값 설정
    latitude = []
    longitude = []
    for accident in accidents:
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)

    # 위치 결과 반환
    return {
        'latitude' : latitude, 
        'longitude' : longitude
    }