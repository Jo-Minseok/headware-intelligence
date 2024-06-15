import os
from fastapi import APIRouter, File, HTTPException, UploadFile, WebSocket, Depends, status
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import List
from starlette.websockets import WebSocketDisconnect
from sqlalchemy.orm import Session
import datetime
from db.db_connection import get_db
from db.models import Accident, UserEmployee
from fcm_notification import fcm_function


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


class AccidentService:
    def __init__(self, db_session: Session):
        self.db_session = db_session

    def create_accident(self, accident: Accident_Json):
        db_accident = Accident(
            date=datetime.date(
                year=accident.date[0], month=accident.date[1], day=accident.date[2]),
            time=datetime.time(
                hour=accident.time[0], minute=accident.time[1], second=accident.time[2]),
            latitude=accident.latitude,
            longitude=accident.longitude,
            work_id=accident.work_id,
            victim_id=accident.victim_id,
            category=accident.category
        )
        self.db_session.add(db_accident)
        self.db_session.commit()
        user = self.db_session.query(UserEmployee).filter(
            UserEmployee.id == accident.victim_id).first()
        if not user:
            raise HTTPException(status_code=404, detail="사용자를 찾을 수 없습니다.")
        fcm_function.fcm_send_messaging(
            accident.work_id, accident.victim_id, self.db_session)
        return {"status": "success"}


class ImageService:
    def __init__(self):
        self.save_path = "./accident/uploaded_images/"
        if not os.path.exists(self.save_path):
            os.makedirs(self.save_path)

    async def upload_image(self, file: UploadFile):
        file_path = os.path.join(self.save_path, file.filename)
        with open(file_path, "wb") as image_file:
            image_file.write(await file.read())
        return {"message": "File uploaded successfully", "filename": file.filename}


class WebSocketManager:
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


accident_service = AccidentService(db_session=Depends(get_db))
image_service = ImageService()
websocket_manager = WebSocketManager()


# 사고 발생시 데이터를 받아오고, 이를 DB에 저장하는 방식
@router.post("/upload", status_code=status.HTTP_200_OK)
def post_accident(accident: Accident_Json, service: AccidentService = Depends(accident_service)):
    return service.create_accident(accident)


@router.post("/upload_image")
async def upload_image(file: UploadFile = File(...), service: ImageService = Depends(image_service)):
    return await service.upload_image(file)


@router.websocket("/ws/{work_id}/{user_id}")
async def websocket_endpoint(websocket: WebSocket, work_id: str, user_id: str):
    await websocket_manager.connect(work_id, websocket)
    try:
        while True:
            data = await websocket.receive_text()
            await websocket_manager.broadcast(work_id, f"{user_id}:{data}")
    except WebSocketDisconnect:
        websocket_manager.disconnect(work_id, websocket)


@router.get('/get_image/{victim}/{manager}')
async def get_image(victim: str, manager: str):
    image_path = os.path.join(
        './accident/uploaded_images/', f"{victim}_{manager}.jpg")
    if os.path.exists(image_path):
        return FileResponse(image_path)
    raise HTTPException(status_code=404, detail='image not found')


@router.get("/emergency")
async def emergency_call(work_id: str, user_id: str, db: Session = Depends(get_db)):
    fcm_function.fcm_send_emergency(work_id, user_id, db)
