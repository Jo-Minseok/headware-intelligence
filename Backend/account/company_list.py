from typing import List
from fastapi import APIRouter, Depends, status
from pydantic import BaseModel
from sqlalchemy.orm import Session
from db.db_connection import get_db
from db.models import CompanyList, Work, Work_list

router = APIRouter(prefix="/company")


class Work_list(BaseModel):
    work_list: List[str] = []


@router.get("/list", status_code=status.HTTP_200_OK)
def get_company_list(db: Session = Depends(get_db)):
    company_list = db.query(CompanyList.company).all()
    return {"companies": [company[0] for company in company_list]}


@router.get("/work_list", response_model=Work_list, status_code=status.HTTP_200_OK)
def get_work_list(user_id: str, db: Session = Depends(get_db)):
    work_rows = db.query(Work).filter(Work.worker_id == user_id)
    work_ids = [work_row.work_id for work_row in work_rows]
    return Work_list(work_list=work_ids)
