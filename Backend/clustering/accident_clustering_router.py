import pandas as pd
import numpy as np
from fastapi import APIRouter, Depends, HTTPException
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score
from db.db_connection import get_db
from sqlalchemy.orm import Session
from clustering import accident_clustering_crud
from starlette import status

router = APIRouter(prefix="/map")
    
@router.get("/cluster")
def cluster_data(db: Session = Depends(get_db)):
    # 사고 발생 데이터 조회
    accidents = accident_clustering_crud.get_accident(db=db)
    
    # 지도 위도, 경도 값 설정(현재 테스트 데이터)
    latitude = []
    longitude = []
    for accident in accidents:
        latitude.append(accident.latitude)
        longitude.append(accident.longitude)
    
    # 불러온 위도 경도 값의 개수가 3개 미만일 경우 예외 리턴
    if len(latitude) < 3:
        raise HTTPException(
            status_code=status.HTTP_204_NO_CONTENT, detail="클러스터를 표시하기에는 데이터가 부족")

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

    # 클러스터 중심점 추출
    centers = kmeans.cluster_centers_
    
    # 클러스터 레이블 추출
    labels = kmeans.labels_

    # 클러스터 중심점과 가장 먼 지점과의 거리 계산
    max_distances = []
    for i in range(size):
        cluster_points = df.loc[labels == i]
        distances = np.linalg.norm(cluster_points[['latitude', 'longitude']] - centers[i], axis=1)
        max_distances.append(np.max(distances))

    # 클러스터 중심점 결과
    res = {i + 1 : list(centers[i]) + [max_distances[i]] for i in range(size)}
    return res