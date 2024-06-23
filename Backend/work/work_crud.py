from sqlalchemy import exists
from sqlalchemy.orm import Session
from db.models import UserEmployee, WorkList, Work
from typing import List
from db.db_connection import get_db
from fastapi import Depends

class WorkRepository:
    def __init__(self, db: Session):
        self.db = db
    
    def get_all_employeeId(self) -> List[str]:
        employeeRows = self.db.query(UserEmployee).all()
        return [employeeRow.id for employeeRow in employeeRows]

    def get_manager_by_workId(self, managerId: str) -> List[int]:
        workRows = self.db.query(WorkList).filter(WorkList.managerId == managerId).all()
        return [workRow.workId for workRow in workRows]
    
    def update_employee_work(self, workId : int, employeeId: str):
        workExists = self.db.query(exists().where(Work.workId == workId).where(Work.workerId == employeeId)).scalar()
        if not workExists:
            newWork = Work(workId=workId, workerId=employeeId)
            self.db.add(newWork)
            self.db.commit()
        return workExists

class WorkService:
    def __init__(self, repository: WorkRepository):
        self.repository = repository
        
    def search_employeeId(self) -> List[str]:
        return self.repository.get_all_employeeId()

    def search_workId(self, managerId: str) -> List[int]:
        return self.repository.get_manager_by_workId(managerId)
    
    def assign_work_employee(self, workId: int, employeeId: str):
        return self.repository.update_employee_work(workId, employeeId)

def get_work_service(db: Session = Depends(get_db)) -> WorkService:
    repository = WorkRepository(db)
    return WorkService(repository)
