from sqlalchemy import exists
from sqlalchemy.orm import Session
from db.models import UserEmployee, WorkList, Work, Accident
from typing import List, Tuple, Optional
from db.db_connection import get_db
from fastapi import Depends
from pydantic import BaseModel, validator
from datetime import datetime


class Validators:
    @staticmethod
    def not_empty(v):
        if not v or not v.strip():
            raise ValueError('빈 값은 허용되지 않습니다.')
        return v


class WorkInputCreate(BaseModel):
    name: str
    company: str
    startDate: datetime
    endDate: Optional[datetime] = None

    @validator('name', 'company', 'startDate', pre=True, always=True)
    def not_empty(cls, v):
        return Validators.not_empty(v)


class WorkRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_work_list(self, managerId: str) -> List[WorkList]:
        return self.db.query(WorkList).filter(WorkList.managerId == managerId).all()

    def create_work(self, managerId: str, data: WorkInputCreate):
        newWorkList = WorkList(name=data.name, company=data.company,
                               startDate=data.startDate, endDate=data.endDate, managerId=managerId)
        self.db.add(newWorkList)
        self.db.commit()

    def get_work(self, workId: int) -> List[Tuple[int, str]]:
        return self.db.query(Work.workerId, UserEmployee.name).join(UserEmployee, Work.workerId == UserEmployee.id).filter(Work.workId == workId).all()

    def update_work(self, workId: int, data: WorkInputCreate):
        updateWorkList = self.db.query(WorkList).filter(
            WorkList.workId == workId).first()
        updateWorkList.name = data.name
        updateWorkList.company = data.company
        updateWorkList.startDate = data.startDate
        updateWorkList.endDate = data.endDate
        self.db.commit()

    def delete_work(self, workId: int):
        accidents = self.db.query(Accident).filter(
            Accident.workId == workId).all()
        for accident in accidents:
            accident.workId = None

        self.db.commit()
        # Delete work record
        deleteWorkList = self.db.query(WorkList).filter(
            WorkList.workId == workId).first()
        if deleteWorkList:
            self.db.delete(deleteWorkList)
            self.db.commit()

    def get_employee(self, employeeId: str) -> UserEmployee:
        return self.db.query(UserEmployee).filter(UserEmployee.id == employeeId).first()

    def create_employee_work(self, workId: int, employeeId: str):
        userExists = self.db.query(exists().where(
            UserEmployee.id == employeeId)).scalar()
        if userExists:
            workExists = self.db.query(exists().where(
                Work.workId == workId).where(Work.workerId == employeeId)).scalar()
            if not workExists:
                newWork = Work(workId=workId, workerId=employeeId)
                self.db.add(newWork)
                self.db.commit()
            return workExists
        return not userExists

    def remove_employee_work(self, workId: int, employeeId: str):
        # Check if the work assignment exists
        assignment = self.db.query(Work).filter(
            Work.workId == workId, Work.workerId == employeeId).first()
        if assignment:
            self.db.delete(assignment)
            self.db.commit()
            return True
        return False

    def unassign_work_in_accidents(self, workId: int, employeeId: str):
        # Find all accidents with the specified workId and victimId (employeeId)
        accidents = self.db.query(Accident).filter(
            Accident.workId == workId, Accident.victimId == employeeId).all()

        # Update each found accident to set workId to NULL
        for accident in accidents:
            accident.workId = None

        self.db.commit()


class WorkService:
    def __init__(self, repository: WorkRepository):
        self.repository = repository

    def search_work_list(self, managerId: str) -> List[WorkList]:
        return self.repository.get_work_list(managerId)

    def insert_work(self, managerId: str, data: WorkInputCreate):
        self.repository.create_work(managerId, data)

    def search_work(self, workId: int) -> List[Tuple[int, str]]:
        return self.repository.get_work(workId)

    def modify_work(self, workId: int, data: WorkInputCreate):
        self.repository.update_work(workId, data)

    def remove_work(self, workId: int):
        self.repository.delete_work(workId)

    def search_employee(self, employeeId: str) -> UserEmployee:
        return self.repository.get_employee(employeeId)

    def assign_employee_work(self, workId: int, employeeId: str):
        return self.repository.create_employee_work(workId, employeeId)

    def unassign_employee_work(self, workId: int, employeeId: str):
        # Remove the work assignment
        success = self.repository.remove_employee_work(workId, employeeId)

        if success:
            # Update accidents to set workId to NULL
            self.repository.unassign_work_in_accidents(workId, employeeId)

        return success


def get_work_service(db: Session = Depends(get_db)) -> WorkService:
    repository = WorkRepository(db)
    return WorkService(repository)
