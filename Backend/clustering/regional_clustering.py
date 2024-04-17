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
latitude = [37.5585, 40.7128, 51.5074, 48.8566, 35.6895]

# 지도 경도 값 설정(현재 테스트 데이터)
longitude = [126.9368, -74.0060, -0.1278, 2.3522, 139.6917]

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
print('Silhouette Score')
for i, j in zip(k_range, silhouette_scores):
    print(i, ':', j)

# 실루엣 스코어 그래프 그리기
plt.plot(k_range, silhouette_scores, 'o-')
plt.xlabel('Number of clusters')
plt.ylabel('Silhouette Score')
plt.title('Silhouette Analysis for Optimal k')
plt.show()