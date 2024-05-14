from fastapi import APIRouter, Depends, Path
from db.db_connection import get_db
from sqlalchemy.orm import Session
from processing import processing_contents_crud

router = APIRouter(prefix='/accident')

@router.get('/processing/{situationCode}')
def processing_data(db: Session = Depends(get_db), situationCode: str = Path(...)):
    # 사고 처리 데이터 조회
    accidents = processing_contents_crud.get_all_accident_processing(db=db, situationCode=situationCode)
    
    # 사고 처리 데이터 처리
    no = []
    date = []
    time = []
    latitude = []
    longitude = []
    category = []
    victimName = []
    situation = []
    processingDate = []
    processingTime = []
    detail = []
    for accident in accidents:
        no.append(accident.no)
        date.append(accident.date)
        time.append(accident.time)
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
        category.append(accident.category)
        victimName.append(accident.name)
        situation.append(accident.situation)
        processingDate.append(accident.date)
        processingTime.append(accident.time)
        detail.append(accident.detail)
    
    # 결과 반환
    return {
        'no' : no, 
        'date' : date, 
        'time' : time, 
        'latitude' : latitude, 
        'longitude' : longitude,
        'category' : category, 
        'victimName' : victimName, 
        'situation' : situation, 
        'processingDate' : processingDate, 
        'processingTime' : processingTime, 
        'detail' : detail
    }
