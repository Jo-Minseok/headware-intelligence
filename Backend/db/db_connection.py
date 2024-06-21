from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from pydantic_settings import BaseSettings, SettingsConfigDict


# .env 파일로 데이터베이스 호스트, 포트 등 정보 연결. GITHUB 업로드 불가
class DBSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./db/.env', env_file_encoding='utf-8')
    dbUsername: str
    dbPassword: str
    dbHost: str
    dbPort: str
    dbName: str


# 제너레이터용 함수
def get_db():
    try:
        db = dbSession()
        yield db
    finally:
        db.close()


# DB 접속 환경 객체 생성
dbEnv = DBSettings(_env_file=r'./db/.env', _env_file_encoding='utf-8')


# 데이터베이스 커넥션 풀 생성
engine = create_engine('mariadb+pymysql://{username}:{password}@{host}:{port}/{dbName}'.format(
    username=dbEnv.dbUsername, password=dbEnv.dbPassword, host=dbEnv.dbHost, port=dbEnv.dbPort, dbName=dbEnv.dbName))


# 데이터베이스에 접속하기 위한 객체
dbSession = sessionmaker(autocommit=False, autoflush=False, bind=engine)
