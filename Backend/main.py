from fastapi import FastAPI
import uvicorn
from weather import weather_api
from account import register_router
from account import login_router
from accident import accident_api
from account import forgot_router
from clustering import accident_clustering_router
from trend import accident_trend_router
from db import models
from db.db_connection import engine

# FastAPI APP 생성
app = FastAPI()


# 백엔드 서버 접속 여부
@app.get("/")
async def main():
    return 200


# 라우터 추가
app.include_router(weather_api.router)
app.include_router(register_router.router)
app.include_router(login_router.router)
app.include_router(forgot_router.router)
app.include_router(accident_clustering_router.router)
app.include_router(accident_trend_router.router)
app.include_router(accident_api.router)

# Main
if __name__ == '__main__':
    # DB 테이블 없을 경우 생성
    models.Base.metadata.create_all(bind=engine)
    # uvicorn 서버 가동
    uvicorn.run(app, host='0.0.0.0', port=8000)
