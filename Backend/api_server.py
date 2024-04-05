from fastapi import FastAPI, Response
import requests, uvicorn, api_config, openpyxl, pytz
from datetime import datetime, timedelta

data = openpyxl.load_workbook('processing data.xlsx')
sheet = data.active

data_dict = {}
for row in sheet.iter_rows(values_only = True):
    data_dict[row[0]] = [row[1], row[2]]

app = FastAPI()

@app.get("/weather/{city}/{district}")
async def get_weather(city: str, district: str):
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
    response = requests.get(api_config.api.api_endpoint + '/getUltraSrtNcst'
                            '?serviceKey=' + api_config.api.api_key + 
                            '&dataType=JSON' + 
                            '&base_date=' + date + 
                            '&base_time=' + base_time + 
                            '&nx=' + data_dict[city + ' ' + district][0] + 
                            '&ny=' + data_dict[city + ' ' + district][1])
    
    return response.json()
    # # 세부 결과 확인
    # weather_data = response.json()['response']['body']['items']['item']
    # result = ('위치 : ' + city + ' ' + district + '\n' + 
    #           '날짜 : ' + weather_data[0]['baseDate'] + '\n' + 
    #           '기준 시간 : ' + weather_data[0]['baseTime'][:2] + '시\n')
    # for i in weather_data:
    #     if i['category'] == 'T1H':
    #         result += '기온 : ' + i['obsrValue'] + ' °C\n'
    #     elif i['category'] == 'RN1':
    #         result += '1시간 강수량 : ' + i['obsrValue'] + ' mm\n'
    #     elif i['category'] == 'UUU':
    #         result += '동서 바람 성분 : ' + i['obsrValue'] + ' m/s\n'
    #     elif i['category'] == 'VVV':
    #         result += '남북 바람 성분 : ' + i['obsrValue'] + ' m/s\n'
    #     elif i['category'] == 'REH':
    #         result += '습도 : ' + i['obsrValue'] + ' %\n'
    #     elif i['category'] == 'PTY':
    #         result += '강수 형태 : ' + i['obsrValue'] + '\n'
    #     elif i['category'] == 'VEC':
    #         result += '풍향 : ' + i['obsrValue'] + ' deg\n'
    #     elif i['category'] == 'WSD':
    #         result += '풍속 : ' + i['obsrValue'] + ' m/s\n'   
    # result = Response(result)
    # result.headers['Content-Type'] = 'text/plain; charset=utf-8'
    # return result

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)