from fastapi import APIRouter, Depends, Path
from db.db_connection import get_db
from sqlalchemy.orm import Session
from processing import processing_contents_crud

router = APIRouter(prefix='/accident')

@router.get('/processing')
def processing_data(db: Session = Depends(get_db)):
    # 사고 처리 데이터 조회(처리 완료, 처리 중, 119 신고)
    accidentProcessings = processing_contents_crud.get_accident_processing(db=db)
    
    # 사고 처리 데이터 처리(처리 완료, 처리 중, 119 신고)
    no = []
    date = []
    time = []
    latitude = []
    longitude = []
    category = []
    victim = []
    situation = []
    processing_date = []
    processing_time = []
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
        processing_date.append(accidentProcessing.date)
        processing_time.append(accidentProcessing.time)
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
        'processing_date' : processing_date, 
        'processing_time' : processing_time, 
        'detail' : detail
    }
    
@router.get('/malfunction')
def processing_data(db: Session = Depends(get_db)):
    # 사고 처리 데이터 조회(오작동)
    accidentProcessingMalfunctions = processing_contents_crud.get_accident_processing_malfunction(db=db)
    
    # 사고 처리 데이터 처리(오작동)
    no = []
    date = []
    time = []
    latitude = []
    longitude = []
    category = []
    victim = []
    situation = []
    processing_date = []
    processing_time = []
    detail = []
    for accidentProcessingMalfunction in accidentProcessingMalfunctions:
        accident = processing_contents_crud.get_accident(db=db, no=accidentProcessingMalfunction.no)
        no.append(accident.no)
        date.append(accident.date)
        time.append(accident.time)
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
        category.append(accident.category)
        victim.append(processing_contents_crud.get_victim_name(db=db, no=accidentProcessingMalfunction.no))
        situation.append(accidentProcessingMalfunction.situation)
        processing_date.append(accidentProcessingMalfunction.date)
        processing_time.append(accidentProcessingMalfunction.time)
        detail.append(accidentProcessingMalfunction.detail)
    
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
        'processing_date' : processing_date, 
        'processing_time' : processing_time, 
        'detail' : detail
    }