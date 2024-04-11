from fastapi import FastAPI
import uvicorn
from weather import weather_api
from user import account
from weather import api_config

app = FastAPI()

app.include_router(weather_api.router)
app.include_router(account.router)
if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)
    