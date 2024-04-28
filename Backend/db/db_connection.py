from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from pydantic_settings import BaseSettings, SettingsConfigDict


# .env 파일로 데이터베이스 호스트, 포트 등 정보 연결. GITHUB 업로드 불가
class DBSettings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'./db/.env', env_file_encoding='utf-8')
    db_username: str
    db_password: str
    db_host: str
    db_port: str
    db_name: str


# 제너레이터용 함수
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


# 해당 클래스를 상속받은 자식 클래스들은 모두 테이블과 매핑되는 클래스로 인식하기 위한 객체 생성
Base = declarative_base()
