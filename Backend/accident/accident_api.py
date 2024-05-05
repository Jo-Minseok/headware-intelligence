from fastapi import APIRouter, WebSocket, Depends
from pydantic import BaseModel
from typing import List
from starlette.websockets import WebSocketDisconnect
from sqlalchemy.orm import Session
import datetime
from db.db_connection import get_db
from db.models import Accident

router = APIRouter(prefix="/accident")


# 사고 발생 Json 구조
class Accident_Json(BaseModel):
    type: str
    date: List[int] = []
    time: List[int] = []


# 사고 발생시 데이터를 받아오고, 이를 DB에 저장하는 방식
@router.post("/upload")
def post_accident(accident: Accident_Json, db: Session = Depends(get_db)):
    db_accident = Accident(no="",
                           date=datetime.date(
                               year=accident.date[0], month=accident.date[1], day=accident.date[2]),
                           time=datetime.time(
                               hour=accident.time[0], minute=accident.time[1], second=accident.time[2]),
                           latitude="",
                           longtitude="",
                           victim_id="",
                           category=accident.type)
    db.add(db_accident)
    db.commit()
    return {"status": "success"}


@ router.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accpet()
    try:
        while True:
            data = await websocket.receive_text()
    except WebSocketDisconnect:
        await websocket.close()
