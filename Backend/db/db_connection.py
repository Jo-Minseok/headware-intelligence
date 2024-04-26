from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from pydantic_settings import BaseSettings, SettingsConfigDict

# .env 파일을 통해 데이터베이스 코드를 공유하지만 도메인, 아이디, 비밀번호 등은 가림.


class DBSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./db/.env', env_file_encoding='utf-8')
    db_username: str
    db_password: str
    db_host: str
    db_port: str
    db_name: str


def get_db():
    try:
        db = db_session()
        yield db
    finally:
        db.close()


# DB 접속 환경 객체 생성
db_env = DBSettings(_env_file=r'./db/.env', _env_file_encoding='utf-8')
# 데이터베이스 커넥션 풀 생성
engine = create_engine('mariadb+pymysql://{username}:{password}@{host}:{port}/{db_name}'.format(
    username=db_env.db_username, password=db_env.db_password, host=db_env.db_host, port=db_env.db_port, db_name=db_env.db_name))
# 데이터베이스에 접속하기 위한 객체
db_session = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()
