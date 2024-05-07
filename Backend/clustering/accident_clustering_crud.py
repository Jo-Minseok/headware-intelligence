from db.models import Accident
from db.db_connection import db_session
from sqlalchemy.orm import Session
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
import pandas as pd

# 사고 발생 데이터 조회
def get_accidents(db: Session):
    return db.query(Accident).all()

# 사고 발생 데이터 군집화(테스트 용도)
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

# insert_accident()

# # 결과 시각화
# # 클러스터 중심점 추출
# centers = kmeans.cluster_centers_

# # 클러스터 레이블 추출
# labels = kmeans.labels_

# # 결과 시각화
# # 실루엣 스코어 그래프 그리기
# plt.plot(k_range, silhouette_scores, 'o-')
# plt.xlabel('Number of clusters')
# plt.ylabel('Silhouette Score')
# plt.title('Silhouette Analysis for Optimal k')
# plt.show()

# # 클러스터링 결과 시각화
# plt.scatter(df['longitude'], df['latitude'], c=labels, cmap='viridis')
# plt.scatter(centers[:, 1], centers[:, 0], marker='x', s=200, c='red')
# plt.xlabel('longitude')
# plt.ylabel('latitude')
# plt.title('K-Means Result')
# plt.show()