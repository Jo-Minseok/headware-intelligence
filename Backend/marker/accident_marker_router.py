from fastapi import APIRouter, Depends, Path
from db.db_connection import get_db
from sqlalchemy.orm import Session
from marker import accident_marker_crud, accident_marker_schema
from common import ReverseSituationCode

router = APIRouter(prefix='/map')
    
@router.get('/marker')
def accident_data(db: Session = Depends(get_db)):
    # 사고 데이터 조회
    accidents = accident_marker_crud.get_all_accident(db=db)
    
    # 데이터 처리
    no = []
    latitude = []
    longitude = []
    for accident in accidents:
        no.append(accident.no)
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
        
    # 사고 처리 데이터 조회
    accidents = accident_marker_crud.get_all_accident_processing(db=db)
    
    # 처리 상황 코드 지정
    situationCode = [ReverseSituationCode[accident.situation] for accident in accidents]

    # 결과 반환
    return {
        'no' : no, 
        'latitude' : latitude, 
        'longitude' : longitude, 
        'situationCode' : situationCode
    }

@router.get('/marker/null')
def accident_data(db: Session = Depends(get_db)):
    # 사고 처리 데이터 조회
    accidents = accident_marker_crud.get_all_accident_processing(db=db)
    
    # 데이터 처리
    no = []
    latitude = []
    longitude = []
    for noNull in [accident.no for accident in accidents if accident.situation == None]:
        accident = accident_marker_crud.get_accident(db=db, no=noNull)
        no.append(accident.no)
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)

    # 결과 반환
    return {
        'no' : no, 
        'latitude' : latitude, 
        'longitude' : longitude
    }

@router.get('/marker/{no}')
def accident_data_detail(db: Session = Depends(get_db), no: str = Path(...)):
    # 사고 처리 데이터 조회
    accident = accident_marker_crud.get_accident_processing(db=db, no=no)
    
    # 사고자 이름 조회
    victim = accident_marker_crud.get_victim_name(db=db, no=no)

    # 결과 반환
    return {
        'no' : accident.no, 
        'situation' : accident.situation, 
        'detail' : accident.detail, 
        'victim' : victim
    }

@router.put('/marker/{no}/{situationCode}')
def accident_processing_change(accident_processing_detail: accident_marker_schema.Accident_Processing_Detail, db: Session = Depends(get_db), no: str = Path(...), situationCode: str = Path(...)):
    # 사고 처리 데이터 갱신
    accident_marker_crud.update_accident_processing(db=db, no=no, situationCode=situationCode, detail=accident_processing_detail)
