from db.models import Accident
from db.db_connection import get_db, db_session
from sqlalchemy.orm import Session
from fastapi import Depends
from datetime import datetime, timedelta
import numpy as np
import pandas as pd
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
import time

# 사고 발생 데이터 불러오기
def get_accident(db: Session = Depends(get_db)):
    return db.query(Accident).all()

# 사고 발생 데이터 삽입(임시)
def insert_accident():
    # db 세션 연결
    db = db_session()
    
    # 데이터 생성 전 데이터 삭제
    db.query(Accident).delete()
    db.commit()
    
    # 부산광역시 남구 유엔로157번길 75 기준 위도, 경도 값
    base_latitude = 35.1336437235
    base_longitude = 129.09320833287

    # 1도의 위도와 경도가 약 111km를 나타내므로, 3km는 대략 3/111도임
    # 반경을 3km로 설정하여 주변에 데이터를 생성
    radius_km = 3 / 111

    # 무작위로 100개의 좌표 생성
    np.random.seed(42) # 재현성을 위해 시드 설정
    latitude = np.random.uniform(low=base_latitude - radius_km, high=base_latitude + radius_km, size=300)
    longitude = np.random.uniform(low=base_longitude - radius_km, high=base_longitude + radius_km, size=300)
    
    # 시작 날짜와 끝 날짜 설정
    start_date = datetime(2023, 5, 1)
    end_date = datetime(2024, 4, 1)

    # 100개의 무작위 정수 생성 (날짜 차이를 일 단위로 나타내는 정수)
    random_days = np.random.randint(0, (end_date - start_date).days + 1, size=300)

    # 시작 날짜에 무작위로 생성된 날짜 차이를 더하여 날짜 생성
    random_dates = [start_date + timedelta(days=int(random_day)) for random_day in random_days]
    
    for lat, lon, day in zip(latitude, longitude, random_dates):
        accident = Accident(date=day.strftime('%Y-%m-%d'), 
                            time=datetime.now().strftime('%H:%M:%S'), 
                            latitude=lat, 
                            longitude=lon, 
                            victim_id='user2', 
                            category='test')
        db.add(accident)

    db.commit()
    db.close()

def model_learning_result():
    # db 세션 연결
    db = db_session()
    
    # 사고 발생 데이터 조회
    accidents = db.query(Accident).all()
    
    # 지도 위도, 경도 값 설정(현재 테스트 데이터)
    latitude = []
    longitude = []
    for accident in accidents:
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
    
    # 데이터프레임 생성
    df = pd.DataFrame({'latitude' : latitude, 'longitude' : longitude})

    # 클러스터 개수 범위 설정, 범위를 위도, 경도 값 개수에 따라 2개에서 n - 1까지 지정(데이터가 5개라면 클러스터 개수는 2~4개가 나와야 함)
    k_range = range(2, len(latitude))

    # 각 클러스터 개수에 대해 KMeans 모델을 훈련하고 실루엣 스코어 계산
    silhouette_scores = []
    for k in k_range:
        kmeans = KMeans(n_clusters=k, random_state=42)
        kmeans.fit(df)
        labels = kmeans.labels_
        silhouette_avg = silhouette_score(df, labels)
        silhouette_scores.append(silhouette_avg)

    # 적절한 클러스터 개수 계산
    size = silhouette_scores.index(max(silhouette_scores)) + 2

    # K-Means 모델 훈련
    kmeans = KMeans(n_clusters=size, random_state=42)
    kmeans.fit(df)

insert_accident()

start_time = time.time()

model_learning_result()

end_time = time.time()

print('time :', end_time - start_time)

'''
# 결과 시각화
# 클러스터 중심점 추출
centers = kmeans.cluster_centers_

# 클러스터 레이블 추출
labels = kmeans.labels_

# 결과 시각화
# 실루엣 스코어 그래프 그리기
plt.plot(k_range, silhouette_scores, 'o-')
plt.xlabel('Number of clusters')
plt.ylabel('Silhouette Score')
plt.title('Silhouette Analysis for Optimal k')
plt.show()

# 클러스터링 결과 시각화
plt.scatter(df['longitude'], df['latitude'], c=labels, cmap='viridis')
plt.scatter(centers[:, 1], centers[:, 0], marker='x', s=200, c='red')
plt.xlabel('longitude')
plt.ylabel('latitude')
plt.title('K-Means Result')
plt.show()
'''