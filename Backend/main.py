from fastapi import FastAPI
import uvicorn
from weather import weather_api
from Backend.user import register
from weather import api_config

app = FastAPI()


@app.get("/")
async def main():
    return 1

app.include_router(weather_api.router)
app.include_router(register.router)
if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)
