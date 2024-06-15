from typing import List
from fastapi import APIRouter, Depends, status
from pydantic import BaseModel
from sqlalchemy.orm import Session
from db.db_connection import get_db
from db.models import CompanyList, Work

router = APIRouter(prefix="/company")


# Pydantic 모델 정의
class WorkList(BaseModel):
    work_list: List[str] = []


# 리포지토리 클래스 정의
class CompanyRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_companies(self) -> List[str]:
        company_list = self.db.query(CompanyList.company).all()
        return [company[0] for company in company_list]

    def get_work_list_by_user_id(self, user_id: str) -> List[str]:
        work_rows = self.db.query(Work).filter(Work.worker_id == user_id).all()
        return [work_row.work_id for work_row in work_rows]


# 서비스 클래스 정의
class CompanyService:
    def __init__(self, repository: CompanyRepository):
        self.repository = repository

    def get_company_list(self) -> List[str]:
        return self.repository.get_all_companies()

    def get_work_list(self, user_id: str) -> WorkList:
        work_ids = self.repository.get_work_list_by_user_id(user_id)
        return WorkList(work_list=work_ids)


# 의존성 주입을 위한 함수
def get_company_service(db: Session = Depends(get_db)) -> CompanyService:
    repository = CompanyRepository(db)
    return CompanyService(repository)


# API 엔드포인트 정의
@router.get("/list", status_code=status.HTTP_200_OK)
def get_company_list(service: CompanyService = Depends(get_company_service)):
    companies = service.get_company_list()
    return {"companies": companies}


@router.get("/work_list", response_model=WorkList, status_code=status.HTTP_200_OK)
def get_work_list(user_id: str, service: CompanyService = Depends(get_company_service)):
    work_list = service.get_work_list(user_id)
    return work_list
