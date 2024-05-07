import base64
import io
from fastapi import APIRouter, WebSocket, Depends, UploadFile, File, Form
from fastapi.responses import JSONResponse
import os
from pydantic import BaseModel
from typing import List
from starlette.websockets import WebSocketDisconnect
from sqlalchemy.orm import Session
import datetime
from db.db_connection import get_db
from db.models import Accident
from io import BytesIO
from PIL import Image

router = APIRouter(prefix="/accident")


# 사고 발생 Json 구조
class Accident_Json(BaseModel):
    type: str
    date: List[int] = []
    time: List[int] = []
    user_id: str


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
@router.post("/upload")
def post_accident(accident: Accident_Json, db: Session = Depends(get_db)):
    db_accident = Accident(date=datetime.date(year=accident.date[0], month=accident.date[1], day=accident.date[2]),
                           time=datetime.time(
                               hour=accident.time[0], minute=accident.time[1], second=accident.time[2]),
                           latitude=0.000000,
                           longtitude=0.000000,
                           victim_id=accident.user_id,
                           category=accident.type)
    db.add(db_accident)
    db.commit()
    return 200


# @router.post("/upload_image")
# async def upload_image(file: UploadFile = File(...)):
#     try:
#         # 파일을 업로드할 경로 설정
#         save_path = "./accident/uploaded_images/"

#         if not os.path.exists(save_path):
#             os.makedirs(save_path)

#         # 이미지 파일을 서버에 저장
#         file_path = os.path.join(save_path, file.filename)
#         with open(file_path, "wb") as f:
#             f.write(await file.read())

#         # 성공적으로 저장되었다는 응답 반환
#         return JSONResponse(content={"message": "File uploaded successfully", "filename": file.filename})
#     except Exception as e:
#         # 오류 발생 시 오류 메시지 반환
#         return JSONResponse(content={"message": str(e)}, status_code=500)


@router.post("/upload_image")
async def upload_image(file: UploadFile = File(...)):
    try:
        # 파일을 업로드할 경로 설정
        save_path = "./accident/uploaded_images/"

        if not os.path.exists(save_path):
            os.makedirs(save_path)

        # 이미지 파일을 서버에 저장
        file_path = os.path.join(save_path, file.filename)
        image_data = await file.read()
        image_data = base64.b64decode(image_data)
        image = Image.open(io.BytesIO(image_data))
        image.save(file_path)

        # 성공적으로 저장되었다는 응답 반환
        return JSONResponse(content={"message": "File uploaded successfully", "filename": file.filename})
    except Exception as e:
        # 오류 발생 시 오류 메시지 반환
        return JSONResponse(content={"message": str(e)}, status_code=500)


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
