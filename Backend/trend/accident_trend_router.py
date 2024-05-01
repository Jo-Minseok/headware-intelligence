from fastapi import APIRouter, Depends, Path
from fastapi.responses import JSONResponse
from db.db_connection import get_db
from sqlalchemy.orm import Session
from trend import accident_trend_crud
from datetime import datetime
from collections import defaultdict
import calendar
import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression

router = APIRouter(prefix="/trend")

@router.get("/{start}/{end}")
def trend_inclination(db: Session = Depends(get_db), start: str = Path(...), end: str = Path(...)):
    # 받아온 년, 월 데이터를 변환
    start_date = datetime.strptime(start, '%Y-%m')
    end_date = datetime.strptime(end, '%Y-%m')
    
    # 시작 날짜를 1일로 지정하고 종료 날짜를 해당 달의 마지막 날로 지정
    start_date = start_date.replace(day=1)
    end_date = end_date.replace(day=calendar.monthrange(end_date.year, end_date.month)[1])
    
    # 사고 발생 데이터를 날짜에 따라 조회
    accidents = accident_trend_crud.get_accidents_by_date_range(db=db, start_date=start_date, end_date=end_date)
    
    # 월별로 사고 발생 데이터를 집계
    date_count = defaultdict(int)
    for accident in accidents:
        date_count[accident.date.strftime("%Y-%m")] += 1
    
    # 월별로 정렬
    date_count = dict(sorted(date_count.items()))
    
    # 데이터를 입력 변수(X)와 타깃 변수(y)로 나눔
    X = np.array([int(i[:i.index('-')]) * 12 + int(i[i.index('-') + 1:]) for i in date_count.keys()]).reshape(-1, 1)
    y = np.array(list(date_count.values()))
    
    # 선형 회귀 모델 생성
    model = LinearRegression()
    
    # 모델 훈련
    model.fit(X, y)
    
    # 그래프 그리기
    plt.figure(figsize=(10, 6))
    plt.plot(X, y, linestyle='-', color='blue', marker='o', label='Line')
    plt.plot(X, model.predict(X), color='red', label='Trend')

    # x 축 눈금 레이블 변경
    plt.xticks(X.flatten(), date_count.keys(), rotation=45)

    # 그래프 그리기
    plt.xlabel('Month')
    plt.ylabel('Accident Count')
    plt.title('Trend of Accident Count')
    plt.legend()
    plt.grid(True)
    plt.show()
    
    # 추세선의 기울기와 절편 추가
    date_count['inclination'] = model.coef_[0]
    date_count['intercept'] = model.intercept_
    
    # json으로 변환하여 반환
    return JSONResponse(content=date_count)

# # /trend/2023-05-01/2024-02-01 형식
# @router.get("/{start}/{end}")
# def trend_inclination(db: Session = Depends(get_db), start: datetime = Path(...), end: datetime = Path(...)):
#     # 사고 발생 데이터를 날짜에 따라 조회
#     accidents = accident_trend_crud.get_accidents_by_date_range(db=db, start_date=start_date, end_date=end_date)
    
#     # 월별로 사고 발생 데이터를 집계
#     date_count = defaultdict(int)
#     for accident in accidents:
#         date_count[accident.date.strftime("%Y-%m")] += 1
    
#     # 월별로 정렬
#     date_count = dict(sorted(date_count.items()))
    
#     # 데이터를 입력 변수(X)와 타깃 변수(y)로 나눔
#     X = np.array([int(i[:i.index('-')]) * 12 + int(i[i.index('-') + 1:]) for i in date_count.keys()]).reshape(-1, 1)
#     y = np.array(list(date_count.values()))
    
#     # 선형 회귀 모델 생성
#     model = LinearRegression()
    
#     # 모델 훈련
#     model.fit(X, y)
    
#     # 그래프 그리기
#     plt.figure(figsize=(10, 6))
#     plt.plot(X, y, linestyle='-', color='blue', marker='o', label='Line')
#     plt.plot(X, model.predict(X), color='red', label='Trend')

#     # x 축 눈금 레이블 변경
#     plt.xticks(X.flatten(), date_count.keys(), rotation=45)

#     # 그래프 그리기
#     plt.xlabel('Month')
#     plt.ylabel('Accident Count')
#     plt.title('Trend of Accident Count')
#     plt.legend()
#     plt.grid(True)
#     plt.show()
    
#     # 추세선의 기울기와 절편 추가
#     date_count['inclination'] = model.coef_[0]
#     date_count['intercept'] = model.intercept_
    
#     # json으로 변환하여 반환
#     return JSONResponse(content=date_count)