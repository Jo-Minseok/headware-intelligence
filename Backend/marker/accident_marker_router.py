from fastapi import APIRouter, Depends, Path
from db.db_connection import get_db
from sqlalchemy.orm import Session
from marker import accident_marker_crud, accident_marker_schema

router = APIRouter(prefix='/map')
    
@router.get('/marker')
def accident_data(db: Session = Depends(get_db)):
    # 사고 발생 데이터 조회
    accidents = accident_marker_crud.get_accidents(db=db)
    
    # 지도 위도, 경도 값 지정
    no = []
    latitude = []
    longitude = []
    for accident in accidents:
        no.append(accident.no)
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
        
    # 사고 처리 데이터 조회
    accidents = accident_marker_crud.get_accident_processings(db=db)
    
    # 프로세스 코드 지정
    codeDict = {'처리 완료' : 0, '처리 중' : 1, '오작동' : 2, '119 신고' : 3}
    processCode = [codeDict[accident.situation] for accident in accidents]

    # 결과 반환
    return {
        'no' : no, 
        'latitude' : latitude, 
        'longitude' : longitude,
        'processCode' : processCode
    }

@router.get('/marker/{no}')
def accident_data_detail(db: Session = Depends(get_db), no: str = Path(...)):
    # 사고 처리 데이터 조회
    accident = accident_marker_crud.get_accident_processing(db=db, no=no)
    
    # 사고자 이름 조회
    victim = accident_marker_crud.get_victim_name(db=db, no=no)

    # 사고 처리 데이터 반환
    return {
        'no' : accident.no, 
        'situation' : accident.situation, 
        'date' : accident.date, 
        'time' : accident.time, 
        'detail' : accident.detail, 
        'victim' : victim
    }
    
@router.post('/marker/{no}/complete')
def accident_processing_complete(accident_processing_detail: accident_marker_schema.Accident_Processing_Detail, db: Session = Depends(get_db), no: str = Path(...)):
    # 사고 처리 데이터 갱신
    accident_marker_crud.update_accident_processing(db=db, no=no, situation='처리 완료', detail=accident_processing_detail)

@router.post('/marker/{no}/{situation}')
def accident_processing_change(db: Session = Depends(get_db), no: str = Path(...), situation: str = Path(...)):
    # 사고 처리 데이터 갱신
    accident_marker_crud.update_accident_processing(db=db, no=no, situation=situation, detail='')