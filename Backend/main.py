from fastapi import FastAPI, status
import uvicorn
from weather import weather_api
from account import register_router
from account import login_router
from account import logout_router
from account import forgot_router
from accident import accident_api
from account import company_list
from marker import accident_marker_router
from trend import accident_trend_router
from processing import processing_contents_router
from db import models
from db.db_connection import engine

# FastAPI APP 생성
app = FastAPI()


# 백엔드 서버 접속 여부
@app.get("/", status_code=status.HTTP_200_OK)
async def main():
    return 200


# 라우터 추가
app.include_router(weather_api.router)
app.include_router(register_router.router)
app.include_router(login_router.router)
app.include_router(logout_router.router)
app.include_router(forgot_router.router)
app.include_router(accident_marker_router.router)
app.include_router(accident_trend_router.router)
app.include_router(processing_contents_router.router)
app.include_router(accident_api.router)
app.include_router(company_list.router)

# Main
if __name__ == '__main__':
    # DB 테이블 없을 경우 생성
    models.Base.metadata.create_all(bind=engine)
    # uvicorn 서버 가동
    uvicorn.run("main:app", host="0.0.0.0", port=8000)
