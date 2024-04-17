import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans

data = np.array([
    [37.7749, -122.4194],  # 샌프란시스코
    [34.0522, -118.2437],  # 로스앤젤레스
    [40.7128, -74.0060],   # 뉴욕
    [41.8781, -87.6298],   # 시카고
    [29.7604, -95.3698],   # 휴스턴
    [33.4484, -112.0740]   # 피닉스
])

kmeans = KMeans(n_clusters=3)
kmeans.fit(data)

centroids = kmeans.cluster_centers_
print("클러스터 중심:", centroids)

labels = kmeans.labels_
print("클러스터 할당:", labels)

plt.scatter(data[:, 1], data[:, 0], c=labels, cmap='viridis')
plt.scatter(centroids[:, 1], centroids[:, 0], marker='x', s=200, c='red')
plt.xlabel('경도')
plt.ylabel('위도')
plt.title('K-Means 클러스터링 결과')
plt.show()