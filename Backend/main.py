from fastapi import FastAPI
import uvicorn
from weather import weather_api
from user import account
from weather import api_config

app = FastAPI()

@app.get("/")
async def main():
    return 1

app.include_router(weather_api.router)
app.include_router(account.router)
if __name__ == '__main__':
    uvicorn.run(app, host='127.0.0.1', port=8000)
    