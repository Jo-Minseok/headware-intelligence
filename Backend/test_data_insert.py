from db.models import Accident, AccidentProcessing
from db.db_connection import db_session
import datetime
import numpy as np

# 사고 발생 데이터 삽입(테스트 용도)


def insert_accident(start=datetime.datetime(2023, 1, 1), end=datetime.datetime(2024, 6, 30), size=400, K=3):
    # db 세션 연결
    db = db_session()

    # 데이터 생성 전 데이터 삭제
    db.query(AccidentProcessing).delete()
    db.commit()
    db.query(Accident).delete()
    db.commit()

    # 부산광역시 남구 유엔로157번길 75 기준 위도, 경도 값
    base_latitude = 35.1336437235
    base_longitude = 129.09320833287

    # 1도의 위도와 경도가 약 111km를 나타내므로, K km는 대략 K/111도임
    # 반경을 K km로 설정하여 주변에 데이터를 생성
    radius_km = K / 111

    # 무작위로 size개의 좌표 생성
    np.random.seed(42)  # 재현성을 위해 시드 설정
    latitude = np.random.uniform(
        low=base_latitude - radius_km, high=base_latitude + radius_km, size=size)
    longitude = np.random.uniform(
        low=base_longitude - radius_km, high=base_longitude + radius_km, size=size)

    # 시작 날짜와 끝 날짜 설정
    start_date = start
    end_date = end

    # size개의 무작위 정수 생성 (날짜 차이를 일 단위로 나타내는 정수)
    random_days = np.random.randint(
        0, (end_date - start_date).days + 1, size=size)

    # 시작 날짜에 무작위로 생성된 날짜 차이를 더하여 날짜 생성
    random_dates = [
        start_date + datetime.timedelta(days=int(random_day)) for random_day in random_days]

    for lat, lon, day in zip(latitude, longitude, random_dates):
        accident = Accident(date=day.strftime('%Y-%m-%d'),
                            time=datetime.datetime.now().strftime('%H:%M:%S'),
                            latitude=lat,
                            longitude=lon,
                            work_id=['22', '104', '105', '106', '44',
                                     '107', '108', '109'][np.random.randint(0, 8)],
                            victim_id=[
                                '1234', '5678'][np.random.randint(0, 2)],
                            category=['낙상 사고', '낙하 사고'][np.random.randint(0, 2)])
        db.add(accident)

    db.commit()

    # 사고 처리 데이터의 번호 값을 지정하기 위해 Accident 테이블의 모든 데이터를 질의
    accidents = db.query(Accident).all()

    nullConut = 15

    for accident in accidents[:-nullConut]:
        idx = np.random.randint(0, 3)
        if idx == 0:
            processing = AccidentProcessing(no=accident.no,
                                            situation='처리 완료',
                                            date=day.strftime('%Y-%m-%d'),
                                            time=datetime.datetime.now().strftime('%H:%M:%S'),
                                            detail='detail' + str(np.random.randint(1, 100)))
        elif idx == 1:
            processing = AccidentProcessing(no=accident.no,
                                            situation='119 신고',
                                            date=day.strftime('%Y-%m-%d'),
                                            time=datetime.datetime.now().strftime('%H:%M:%S'),
                                            detail=None)
        else:
            processing = AccidentProcessing(no=accident.no,
                                            situation='처리 중',
                                            date=day.strftime('%Y-%m-%d'),
                                            time=datetime.datetime.now().strftime('%H:%M:%S'),
                                            detail=None)

        db.add(processing)

    for accident in accidents[-nullConut:]:
        idx = np.random.randint(0, 3)
        processing = AccidentProcessing(no=accident.no)

        db.add(processing)

    db.commit()
    db.close()

# insert_accident()

# metaData = MetaData()
# metaData.reflect(bind=engine)
# table = Table('accident_processing', metaData, autoload=True, autoload_with=engine)

# with engine.connect() as connection:
#     connection.execute(text("ALTER TABLE accident_processing MODIFY COLUMN situation VARCHAR(100)"))
#     connection.execute(text("ALTER TABLE accident_processing MODIFY COLUMN date DATE"))
#     connection.execute(text("ALTER TABLE accident_processing MODIFY COLUMN time TIME"))
#     connection.execute(text("ALTER TABLE accident_processing MODIFY COLUMN detail VARCHAR(100)"))
