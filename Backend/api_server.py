from fastapi import FastAPI
import requests, uvicorn, api_config, openpyxl, pytz
from datetime import datetime

data = openpyxl.load_workbook('processing data.xlsx')
sheet = data.active

data_dict = {}
for row in sheet.iter_rows(values_only = True):
    data_dict[row[0]] = [row[1], row[2]]

app = FastAPI()

@app.get("/weather/{city}/{district}")
async def get_weather(city: str, district: str):
    now = datetime.now(pytz.timezone('Asia/Seoul'))
    h = now.hour
    m = now.minute
    base_time = ('%02d' % h) + '00' if m > 40 else ('%02d' % (h - 1 if h > 0 else 23)) + '00'
    response = requests.get(api_config.api.api_endpoint + '/getUltraSrtNcst'
                            '?serviceKey=' + api_config.api.api_key + 
                            '&dataType=JSON' + 
                            '&base_date=' + datetime.today().strftime('%Y%m%d') + 
                            '&base_time=' + base_time + 
                            '&nx=' + data_dict[city + ' ' + district][0] + 
                            '&ny=' + data_dict[city + ' ' + district][1])
    return response.json()

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)