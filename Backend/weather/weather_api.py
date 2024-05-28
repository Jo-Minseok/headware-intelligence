import openpyxl
import pytz
from fastapi import APIRouter, status
from datetime import datetime, timedelta
import requests
from weather import api_config
from haversine import haversine, Unit

router = APIRouter(prefix='/weather')

data = openpyxl.load_workbook(r'.\weather\processing data.xlsx')
sheet = data.active

data_dict = {}
for row in sheet.iter_rows(values_only=True):
    data_dict[row[0]] = [row[1], row[2], row[3], row[4]]

@router.get('/{latitude}/{longitude}', status_code=status.HTTP_200_OK)
async def get_weather(latitude: float, longitude: float):
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
    min_distance = float('inf')
    key = None
    for k, v in data_dict.items():
        distance = haversine((latitude, longitude), (float(v[2]), float(v[3])), unit=Unit.METERS)
        if distance < min_distance:
            min_distance = distance
            key = k
    response = requests.get(api_config.api.api_endpoint + '/getUltraSrtNcst'
                            '?serviceKey=' + api_config.api.api_key +
                            '&dataType=JSON' +
                            '&base_date=' + date +
                            '&base_time=' + base_time +
                            '&nx=' + data_dict[key][0] +
                            '&ny=' + data_dict[key][1])
    
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
