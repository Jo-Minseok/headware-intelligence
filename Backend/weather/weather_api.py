import openpyxl
import pytz
from fastapi import APIRouter, status
from datetime import datetime, timedelta
import requests
from weather import api_config
from geopy.geocoders import Nominatim
from geopy.exc import GeocoderServiceError

router = APIRouter(prefix='/weather')

data = openpyxl.load_workbook(r'.\weather\processing data.xlsx')
sheet = data.active

data_dict = {}
for row in sheet.iter_rows(values_only=True):
    data_dict[row[0]] = [row[1], row[2]]

def reverse_geocode(lat, lon):
    try:
        geolocator = Nominatim(user_agent="headmetal")
        location = geolocator.reverse((lat, lon), exactly_one=True)
        result = list(location.address.split(', '))
        return [result[-3], result[-4]]
    except GeocoderServiceError as e:
        print(f"Geocoder service error: {e}")
        return None

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
    address = reverse_geocode(latitude, longitude)
    response = requests.get(api_config.api.api_endpoint + '/getUltraSrtNcst'
                            '?serviceKey=' + api_config.api.api_key +
                            '&dataType=JSON' +
                            '&base_date=' + date +
                            '&base_time=' + base_time +
                            '&nx=' + data_dict[' '.join(address)][0] +
                            '&ny=' + data_dict[' '.join(address)][1])
    
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
