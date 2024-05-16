from typing import Optional
from pydantic import BaseModel

# 사고 처리 세부 내역 작성 스키마
class Accident_Processing_Detail(BaseModel):
    detail: Optional[str] = None