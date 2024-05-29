from fastapi import APIRouter, HTTPException, WebSocket, Depends, status
from pydantic import BaseModel
from typing import List
from pydantic_settings import BaseSettings, SettingsConfigDict
from starlette.websockets import WebSocketDisconnect
from sqlalchemy.orm import Session
import datetime
from db.db_connection import get_db
from db.models import Accident, Work
from pyfcm import FCMNotification
from db.models import UserEmployee

router = APIRouter(prefix="/accident")


# 사고 발생 Json 구조
class Accident_Json(BaseModel):
    category: str
    date: List[int] = []
    time: List[int] = []
    latitude: float
    longitude: float
    work_id: str
    victim_id: str


class Alert(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./accident/.env', env_file_encoding='utf-8')
    api_key: str


FCM_API_KEY = Alert(_env_file=r'./accident/.env', _env_file_encoding='utf-8')


class Work_list(BaseModel):
    work_list: List[str] = []

# Websocket 접속 매니저


class ConnectionManager:
    def __init__(self):
        self.active_connections = {}

    async def connect(self, work_id: str, websocket: WebSocket):
        await websocket.accept()
        if work_id in self.active_connections:
            self.active_connections[work_id].append(websocket)
        else:
            self.active_connections[work_id] = [websocket]

    def disconnect(self, work_id: str, websocket: WebSocket):
        self.active_connections[work_id].remove(websocket)
        if not self.active_connections[work_id]:
            del self.active_connections[work_id]

    async def broadcast(self, work_id: str, message: str):
        if work_id in self.active_connections:
            for connection in self.active_connections[work_id]:
                await connection.send_text(message)


# 사고 발생시 데이터를 받아오고, 이를 DB에 저장하는 방식
@router.post("/upload", status_code=status.HTTP_200_OK)
def post_accident(accident: Accident_Json, db: Session = Depends(get_db)):
    db_accident = Accident(date=datetime.date(year=accident.date[0], month=accident.date[1], day=accident.date[2]),
                           time=datetime.time(
                               hour=accident.time[0], minute=accident.time[1], second=accident.time[2]),
                           latitude=accident.latitude,
                           longitude=accident.longitude,
                           work_id=accident.work_id,
                           victim_id=accident.victim_id,
                           category=accident.category)
    db.add(db_accident)
    db.commit()
    user = db.query(UserEmployee).filter(
        UserEmployee.id == accident.victim_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="사용자를 찾을 수 없습니다.")
    # 사고 발생 시 알림 전송
    push_service = FCMNotification(FCM_API_KEY.api_key)
    alert = push_service.multiple_devices_data_message(
        topic_name=accident.work_id,
        message_title=f"{accident.category} 사고 발생!",
        message_body=f"피해자: {user.name} ({accident.victim_id})"
    )
    return {"status": "success"}


@router.get("/work_list", response_model=Work_list, status_code=status.HTTP_200_OK)
def get_work_list(user_id: str, db: Session = Depends(get_db)):
    work_rows = db.query(Work).filter(Work.worker_id == user_id)
    work_ids = [work_row.work_id for work_row in work_rows]
    return Work_list(work_list=work_ids)


manager = ConnectionManager()


@router.websocket("/ws/{work_id}/{user_id}")
async def websocket_endpoint(websocket: WebSocket, work_id: str, user_id: str):
    await manager.connect(work_id, websocket)  # client websocket 접속 허용
    try:
        while True:
            data = await websocket.receive_text()  # client 메시지 수신 대기
            await manager.broadcast(work_id, f"{user_id}:{data}")
    except WebSocketDisconnect:
        manager.disconnect(work_id, websocket)
