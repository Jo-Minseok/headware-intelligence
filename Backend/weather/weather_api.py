import openpyxl, pytz, requests
from fastapi import APIRouter, HTTPException, status
from datetime import datetime, timedelta
from weather import weather_api_config
from haversine import haversine, Unit

router = APIRouter(prefix='/weather')

def get_current_time():
    now = datetime.now(pytz.timezone('Asia/Seoul'))
    h, m = now.hour, now.minute
    if m > 40:
        base_time = ('%02d' % h) + '00'
        date = datetime.today().strftime('%Y%m%d')
    else:
        if h == 0:
            base_time = '2300'
            date = (datetime.today() - timedelta(days=1)).strftime('%Y%m%d')
        else:
            base_time = ('%02d' % (h - 1)) + '00'
            date = datetime.today().strftime('%Y%m%d')
    return base_time, date

def get_grid(latitude, longitude):
    sheet = openpyxl.load_workbook(r'.\weather\processing data.xlsx').active
    minDistance = float('inf')
    gridX = gridY = None
    for row in sheet.iter_rows(values_only=True):
        distance = haversine((latitude, longitude), (float(row[3]), float(row[4])), unit=Unit.METERS)
        if distance < minDistance:
            minDistance = distance
            gridX = row[1]
            gridY = row[2]
    return gridX, gridY

@router.get('/{latitude}/{longitude}', status_code=status.HTTP_200_OK)
async def get_weather(latitude: float, longitude: float):
    baseTime, date = get_current_time()
    gridX, gridY = get_grid(latitude, longitude)
    
    if not gridX or not gridY:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail='위도 경도 값이 잘못되었습니다.')
    
    response = requests.get(weather_api_config.api.api_endpoint + '/getUltraSrtNcst'
                            '?serviceKey=' + weather_api_config.api.api_key +
                            '&dataType=JSON' +
                            '&base_date=' + date +
                            '&base_time=' + baseTime +
                            '&nx=' + gridX +
                            '&ny=' + gridY)
    
    for i in response.json()['response']['body']['items']['item']:
        if i['category'] == 'T1H':
            temperature = float(i['obsrValue'])
        elif i['category'] == 'WSD':
            airVelocity = float(i['obsrValue'])
        elif i['category'] == 'RN1':
            precipitation = float(i['obsrValue'])
        elif i['category'] == 'REH':
            humidity = float(i['obsrValue'])

    return {
        'temperature' : temperature,
        'airVelocity' : airVelocity,
        'precipitation' : precipitation,
        'humidity' : humidity
    }
