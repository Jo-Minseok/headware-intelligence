from fastapi import APIRouter, Depends, HTTPException, status
from work.work_crud import get_work_service
from work.work_crud import WorkService

router = APIRouter(prefix='/work')

@router.get('/search/employee', status_code=status.HTTP_200_OK)
def get_employee_id(service: WorkService = Depends(get_work_service)):
    searchResult = service.search_employeeId()
    if not searchResult:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail='근로자가 존재하지 않음')
    return {'employeeId': searchResult}

@router.get('/search/{managerId}', status_code=status.HTTP_200_OK)
def get_work_id(managerId: str, service: WorkService = Depends(get_work_service)):
    searchResult = service.search_workId(managerId)
    if not searchResult:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail='관리자에게 할당된 작업이 존재하지 않음')
    return {'workId': searchResult}

@router.put('/assign/{workId}/{employeeId}', status_code=status.HTTP_200_OK)
def update_work(workId: int, employeeId: str, service: WorkService = Depends(get_work_service)):
    updateFailed = service.assign_work_employee(workId, employeeId)
    if updateFailed:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail='이미 할당된 작업')
