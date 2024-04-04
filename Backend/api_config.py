from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file='.env', env_file_encoding='utf-8')
    api_endpoint: str
    api_key: str

api = Settings(_env_file='.env', _env_file_encoding='utf-8')