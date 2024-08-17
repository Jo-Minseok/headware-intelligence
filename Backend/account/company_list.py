from typing import List
from fastapi import APIRouter, Depends, status
from pydantic import BaseModel
from sqlalchemy.orm import Session
from db.db_connection import get_db
from db.models import CompanyList, Work

router = APIRouter(prefix="/company")


# Pydantic 모델 정의
class WorkList(BaseModel):
    workList: List[str] = []


# 리포지토리 클래스 정의
class CompanyRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_all_companies(self) -> List[str]:
        companyList = self.db.query(CompanyList.company).all()
        return [company[0] for company in companyList]

    def get_work_list_by_user_id(self, userId: str) -> List[str]:
        workRows = self.db.query(Work).filter(Work.workerId == userId).all()
        return [str(workRow.workId) for workRow in workRows]


# 서비스 클래스 정의
class CompanyService:
    def __init__(self, repository: CompanyRepository):
        self.repository = repository

    def get_company_list(self) -> List[str]:
        return self.repository.get_all_companies()

    def get_work_list(self, userId: str) -> WorkList:
        workIds = self.repository.get_work_list_by_user_id(userId)
        return WorkList(workList=workIds)


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
def get_work_list(userId: str, service: CompanyService = Depends(get_company_service)):
    workList = service.get_work_list(userId)
    return workList
