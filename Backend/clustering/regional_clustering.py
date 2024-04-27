import pandas as pd
import matplotlib.pyplot as plt
from fastapi import APIRouter
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score

router = APIRouter(prefix="/map")

@router.get("/cluster")
def cluster_data():
    # 모델 제작 완료 후 기입
    return {"result": "clustered data"}

# 지도 위도 값 설정(현재 테스트 데이터)
latitude = [37.5585, 40.7128, 51.5074, 48.8566, 35.6895, 52.5200, 55.7558, 41.8781, 45.4215, 34.0522, 31.2304, 48.8566, 38.9072, 37.7749, 55.6761, 53.3498, 49.2827, 36.7783]

# 지도 경도 값 설정(현재 테스트 데이터)
longitude = [126.9368, -74.0060, -0.1278, 2.3522, 139.6917, 13.4050, 37.6176, -87.6298, -75.6972, -118.2437, 121.4737, 2.3522, -77.0369, -122.4194, 12.5683, -6.2603, -123.1207, -119.4179]

# 데이터프레임 생성
df = pd.DataFrame({'latitude' : latitude, 'longitude' : longitude})
print(df)

# 데이터 추가
# df = pd.concat([df, pd.DataFrame({'latitude' : [], 'longitude' : []})], ignore_index = True)

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

# 실루엣 스코어 출력
print('\nSilhouette Score')
for i, j in zip(k_range, silhouette_scores):
    print(i, ':', j)

# 실루엣 스코어 그래프 그리기
plt.plot(k_range, silhouette_scores, 'o-')
plt.xlabel('Number of clusters')
plt.ylabel('Silhouette Score')
plt.title('Silhouette Analysis for Optimal k')
plt.show()

# K-Means 모델 훈련
kmeans = KMeans(n_clusters=k_range[silhouette_scores.index(max(silhouette_scores))], random_state=42)  # 클러스터 개수는 적절히 설정
kmeans.fit(df)

# 클러스터 중심점 추출
centroids = kmeans.cluster_centers_

# 클러스터 레이블 추출
labels = kmeans.labels_

# 클러스터링 결과 시각화
plt.scatter(df['longitude'], df['latitude'], c=labels, cmap='viridis')
plt.scatter(centroids[:, 1], centroids[:, 0], marker='x', s=200, c='red')
plt.xlabel('longitude')
plt.ylabel('latitude')
plt.title('K-Means Result')
plt.show()