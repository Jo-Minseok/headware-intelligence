from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file='.env', env_file_encoding='utf-8')
    API_KEY_ENCODE: str
    API_KEY_DECODE: str


settings = Settings(_env_file='.env', _env_file_encoding='utf-8')
print(settings.API_KEY_DECODE)
