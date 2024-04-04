from pydantic_settings import BaseSettings, SettingsConfigDict
from fastapi import FastAPI
import requests

# 기상청 API load
class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file='.env', env_file_encoding='utf-8')
    API_KEY_ENCODE: str
    API_KEY_DECODE: str

settings = Settings(_env_file='.env', _env_file_encoding='utf-8')

# FastAPI 백엔드 서버 단
app = FastAPI()

api_endpoint = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0'

@app.get("/weather/{city}")
async def get_weather(city: str):
    response = requests.get(f"{api_endpoint}/weather?q={city}&appid=" + settings.API_KEY_ENCODE)
    weather_data = response.json()
    return weather_data