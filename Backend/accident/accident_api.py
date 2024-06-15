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
    workId: str
    victimId: str


class AccidentService:
    def __init__(self, dbSession: Session):
        self.dbSession = dbSession

    def create_accident(self, accident: Accident_Json):
        db_accident = Accident(
            date=datetime.date(
                year=accident.date[0], month=accident.date[1], day=accident.date[2]),
            time=datetime.time(
                hour=accident.time[0], minute=accident.time[1], second=accident.time[2]),
            latitude=accident.latitude,
            longitude=accident.longitude,
            workId=accident.workId,
            victimId=accident.victimId,
            category=accident.category
        )
        self.dbSession.add(db_accident)
        self.dbSession.commit()
        user = self.dbSession.query(UserEmployee).filter(
            UserEmployee.id == accident.victimId).first()
        if not user:
            raise HTTPException(status_code=404, detail="사용자를 찾을 수 없습니다.")
        fcm_function.fcm_send_messaging(
            accident.workId, accident.victimId, self.dbSession)
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

    async def connect(self, workId: str, websocket: WebSocket):
        await websocket.accept()
        if workId in self.active_connections:
            self.active_connections[workId].append(websocket)
        else:
            self.active_connections[workId] = [websocket]

    def disconnect(self, workId: str, websocket: WebSocket):
        self.active_connections[workId].remove(websocket)
        if not self.active_connections[workId]:
            del self.active_connections[workId]

    async def broadcast(self, workId: str, message: str):
        if workId in self.active_connections:
            for connection in self.active_connections[workId]:
                await connection.send_text(message)


def get_accident_service(db: Session = Depends(get_db)) -> AccidentService:
    return AccidentService(db)


def get_image_service() -> ImageService:
    return ImageService()


def get_websocket_manager() -> WebSocketManager:
    return WebSocketManager()


# 사고 발생시 데이터를 받아오고, 이를 DB에 저장하는 방식
@router.post("/upload", status_code=status.HTTP_200_OK)
def post_accident(accident: Accident_Json, service: AccidentService = Depends(get_accident_service)):
    return service.create_accident(accident)


@router.post("/upload_image")
async def upload_image(file: UploadFile = File(...), service: ImageService = Depends(get_image_service)):
    return await service.upload_image(file)


@router.websocket("/ws/{work_id}/{user_id}")
async def websocket_endpoint(websocket: WebSocket, workId: str, userId: str, manager: WebSocketManager = Depends(get_websocket_manager)):
    await manager.connect(workId, websocket)
    try:
        while True:
            data = await websocket.receive_text()
            await manager.broadcast(workId, f"{userId}:{data}")
    except WebSocketDisconnect:
        manager.disconnect(workId, websocket)


@router.get('/get_image/{victim}/{manager}')
async def get_image(victim: str, manager: str):
    image_path = os.path.join(
        './accident/uploaded_images/', f"{victim}_{manager}.jpg")
    if os.path.exists(image_path):
        return FileResponse(image_path)
    raise HTTPException(status_code=404, detail='image not found')


@router.get("/emergency")
async def emergency_call(workId: str, userId: str, db: Session = Depends(get_db)):
    fcm_function.fcm_send_emergency(workId, userId, db)
