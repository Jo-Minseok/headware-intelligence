from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import scoped_session, sessionmaker
from pydantic_settings import BaseSettings, SettingsConfigDict

# .env


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'.env', env_file_encoding='utf-8')
    username: str
    password: str
    host: str
    port: str
    db_name: str


def db_connect():
    db = db_session()
    status: int
    try:
        yield db
    finally:
        db.close()


db_env = Settings()
engine = create_engine('mariadb://{username}:{password}@{host}:{port}/{db_name}'.format(
    username=db_env.username, password=db_env.password, host=db_env.host, port=db_env.port, db_name=db_env.db_name))
db_session = scoped_session(sessionmaker(
    autocommit=False, autoflush=False, bind=engine))

Base = declarative_base()
