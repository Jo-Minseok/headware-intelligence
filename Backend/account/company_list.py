from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.db_connection import get_db
from db.models import CompanyList

router = APIRouter(prefix="/company")


@router.get("/list")
def get_company_list(db: Session = Depends(get_db)):
    company_list = db.query(CompanyList.company).all()
    return {"companies": [company[0] for company in company_list]}
