from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=r'C:\Users\MINSEOK\Desktop\Git Kraken\headware-intelligence\Backend\weather\.env', env_file_encoding='utf-8')
    api_endpoint: str
    api_key: str


api = Settings(_env_file=r'C:\Users\MINSEOK\Desktop\Git Kraken\headware-intelligence\Backend\weather\.env', _env_file_encoding='utf-8')
