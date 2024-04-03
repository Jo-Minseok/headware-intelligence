from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Union
from fastapi import FastAPI

# 기상청 API load


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file='.env', env_file_encoding='utf-8')
    API_KEY_ENCODE: str
    API_KEY_DECODE: str


settings = Settings(_env_file='.env', _env_file_encoding='utf-8')
print(settings.API_KEY_DECODE)

# FastAPI 백엔드 서버 단
app = FastAPI()


@app.get("/")
def read_root():
    return {"Hello": "World"}


@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id": item_id, "q": q}
