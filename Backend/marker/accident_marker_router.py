from fastapi import APIRouter, Depends, Path
from db.db_connection import get_db
from sqlalchemy.orm import Session
from marker import accident_marker_crud, accident_marker_schema
from common import ReverseSituationCode

router = APIRouter(prefix='/map')


@router.get('/{manager}/marker')
def accident_data(db: Session = Depends(get_db), manager: str = Path(...)):
    # 사고 처리 데이터 조회
    accidents = accident_marker_crud.get_all_accident_processing(
        db=db, manager=manager)

    # 데이터 처리
    no = []
    latitude = []
    longitude = []
    situationCode = []
    workId = []
    try:
        for No in [accident.no for accident in accidents if accident.situation != None]:
            accident = accident_marker_crud.get_accident(db=db, no=No)
            no.append(accident.no)
            latitude.append(accident.latitude)
            longitude.append(accident.longitude)
            workId.append(accident.workId)
        for accident in accidents:
            if accident.situation != None:
                situationCode.append(ReverseSituationCode[accident.situation])
    except AttributeError:
        pass

    # 결과 반환
    return {
        'no': no,
        'latitude': latitude,
        'longitude': longitude,
        'situationCode': situationCode,
        'workId': workId
    }


@router.get('/{manager}/marker/null')
def accident_data(db: Session = Depends(get_db), manager: str = Path(...)):
    # 사고 처리 데이터 조회
    accidents = accident_marker_crud.get_all_accident_processing(
        db=db, manager=manager)

    for accident in accidents:
        if accident is None:
            print("None")
        else:
            print(
                f"AccidentProcessing: no={accident.no}, situation={accident.situation}, date={accident.date}, time={accident.time}, detail={accident.detail}")
    print(accidents)
    # 데이터 처리
    no = []
    latitude = []
    longitude = []
    workId = []
    try:
        for nullNo in [accident.no for accident in accidents if accident.situation == None]:
            accident = accident_marker_crud.get_accident(db=db, no=nullNo)
            no.append(accident.no)
            latitude.append(accident.latitude)
            longitude.append(accident.longitude)
            workId.append(accident.workId)
    except AttributeError:
        pass

    # 결과 반환
    return {
        'no': no,
        'latitude': latitude,
        'longitude': longitude,
        'workId': workId
    }


@router.get('/marker/{no}')
def accident_data_detail(db: Session = Depends(get_db), no: str = Path(...)):
    # 사고 처리 데이터 조회
    accident = accident_marker_crud.get_accident_processing(db=db, no=no)

    # 사고자 조회
    victimId, victimName = accident_marker_crud.get_victim_name(db=db, no=no)

    # 결과 반환
    return {
        'no': accident.no,
        'situation': accident.situation,
        'detail': accident.detail,
        'victimId': victimId,
        'victimName': victimName
    }


@router.put('/marker/{no}/{situationCode}')
def accident_processing_change(accident_processing_detail: accident_marker_schema.Accident_Processing_Detail, db: Session = Depends(get_db), no: str = Path(...), situationCode: str = Path(...)):
    # 사고 처리 데이터 갱신
    accident_marker_crud.update_accident_processing(
        db=db, no=no, situationCode=situationCode, detail=accident_processing_detail)
