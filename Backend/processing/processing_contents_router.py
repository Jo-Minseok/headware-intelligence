from fastapi import APIRouter, Depends, Path
from db.db_connection import get_db
from sqlalchemy.orm import Session
from processing import processing_contents_crud

router = APIRouter(prefix='/accident')

@router.get('/processing/{_situation}')
def processing_data(db: Session = Depends(get_db), _situation: str = Path(...)):
    if _situation != 'malfunction':
        # 사고 처리 데이터 조회(처리 완료, 처리 중, 119 신고)
        accidentProcessings = processing_contents_crud.get_accident_processing(db=db)
    else:
        # 사고 처리 데이터 조회(오작동)
        accidentProcessings = processing_contents_crud.get_accident_processing_malfunction(db=db)
    
    # 사고 처리 데이터 처리
    no = []
    date = []
    time = []
    latitude = []
    longitude = []
    category = []
    victim = []
    situation = []
    processingDate = []
    processingTime = []
    detail = []
    for accidentProcessing in accidentProcessings:
        accident = processing_contents_crud.get_accident(db=db, no=accidentProcessing.no)
        no.append(accident.no)
        date.append(accident.date)
        time.append(accident.time)
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
        category.append(accident.category)
        victim.append(processing_contents_crud.get_victim_name(db=db, no=accidentProcessing.no))
        situation.append(accidentProcessing.situation)
        processingDate.append(accidentProcessing.date)
        processingTime.append(accidentProcessing.time)
        detail.append(accidentProcessing.detail)
    
    # 결과 반환
    return {
        'no' : no, 
        'date' : date, 
        'time' : time, 
        'latitude' : latitude, 
        'longitude' : longitude,
        'category' : category, 
        'victim' : victim, 
        'situation' : situation, 
        'processingDate' : processingDate, 
        'processingTime' : processingTime, 
        'detail' : detail
    }
