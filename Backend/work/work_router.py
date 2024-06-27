from fastapi import APIRouter, Depends, HTTPException, status
from work.work_crud import get_work_service
from work.work_crud import WorkService, WorkInputCreate

router = APIRouter(prefix='/work')

@router.get('/search/{managerId}', status_code=status.HTTP_200_OK)
def get_work_list(managerId: str, service: WorkService = Depends(get_work_service)):
    searchResult = service.search_work_list(managerId)
    if not searchResult:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail='관리자에게 할당된 작업이 존재하지 않음')
    workId = []
    name = []
    company = []
    startDate = []
    endDate = []
    for work in searchResult:
        workId.append(work.workId)
        name.append(work.name)
        company.append(work.company)
        startDate.append(work.startDate)
        endDate.append(work.endDate)
    return {
        'workId': workId, 
        'name': name, 
        'company': company, 
        'startDate': startDate, 
        'endDate': endDate
        }

@router.post('/create/{managerId}', status_code=status.HTTP_200_OK)
def create_work(managerId: str, inputData: WorkInputCreate, service: WorkService = Depends(get_work_service)):
    try:
        service.insert_work(managerId, inputData)
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail=str(e))

@router.get('/search/detail/{workId}', status_code=status.HTTP_200_OK)
def get_work(workId: int, service: WorkService = Depends(get_work_service)):
    searchResult = service.search_work(workId)
    workerId = []
    name = []
    for detail in searchResult:
        workerId.append(detail[0])
        name.append(detail[1])
    return {
        'workerId': workerId, 
        'name': name
        }

@router.post('/update/{workId}', status_code=status.HTTP_200_OK)
def update_work(workId: int, inputData: WorkInputCreate, service: WorkService = Depends(get_work_service)):
    try:
        service.modify_work(workId, inputData)
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail=str(e))

@router.put('/delete/{workId}', status_code=status.HTTP_200_OK)
def delete_work(workId: int, service: WorkService = Depends(get_work_service)):
    service.remove_work(workId)

@router.get('/user/{employeeId}', status_code=status.HTTP_200_OK)
def get_employee(employeeId: str, service: WorkService = Depends(get_work_service)):
    searchResult = service.search_employee(employeeId)
    return {
        'name': searchResult.name, 
        'phoneNo': searchResult.phoneNo
    }

@router.put('/assign/{workId}/{employeeId}', status_code=status.HTTP_200_OK)
def create_employee_work(workId: int, employeeId: str, service: WorkService = Depends(get_work_service)):
    updateFailed = service.assign_employee_work(workId, employeeId)
    if updateFailed:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail='존재하지 않는 근로자거나 이미 할당된 작업임')
