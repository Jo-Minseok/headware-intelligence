from sqlalchemy import Column, String, Integer, Date, Time, ForeignKey, Double, CheckConstraint
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()


# 회사 목록 테이블
class CompanyList(Base):
    __tablename__ = 'companyList'
    company = Column(String(100), primary_key=True)


# 안전 관리자 회원 테이블
class UserManager(Base):
    __tablename__ = 'userManager'
    id = Column(String(100), primary_key=True)
    password = Column(String(100), nullable=False)
    name = Column(String(16), nullable=False)
    email = Column(String(100), unique=True, nullable=False)
    phoneNo = Column(String(15), nullable=False)
    company = Column(String(100), ForeignKey('companyList.company'))
    alertToken = Column(String(200))
    loginToken = Column(String(200))


# 작업 목록 테이블
class WorkList(Base):
    __tablename__ = 'workList'
    workId = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(100), nullable=False)
    company = Column(String(100), ForeignKey(
        'companyList.company'), nullable=False)
    startDate = Column(Date, nullable=False)
    endDate = Column(Date)
    managerId = Column(String(100), ForeignKey(
        'userManager.id'), nullable=False)


# 근로자 테이블
class UserEmployee(Base):
    __tablename__ = 'userEmployee'
    id = Column(String(100), primary_key=True)
    password = Column(String(100), nullable=False)
    name = Column(String(16), nullable=False)
    email = Column(String(100), unique=True, nullable=False)
    phoneNo = Column(String(15), nullable=False)
    company = Column(String(100), ForeignKey('companyList.company'))
    alertToken = Column(String(200))
    loginToken = Column(String(200))


# 작업 참가 내역 테이블
class Work(Base):
    __tablename__ = 'work'
    workId = Column(Integer, ForeignKey('workList.workId'), primary_key=True)
    workerId = Column(String(100), ForeignKey(
        'userEmployee.id'), primary_key=True)


# 사고 발생 테이블
class Accident(Base):
    __tablename__ = 'accident'
    no = Column(Integer, primary_key=True, autoincrement=True)
    date = Column(Date, nullable=False)
    time = Column(Time, nullable=False)
    latitude = Column(Double, CheckConstraint(
        'latitude >= -90.000000 AND latitude <= 90.000000'), nullable=False, default=1.0)
    longitude = Column(Double, CheckConstraint(
        'longitude >= -180.000000 AND longitude <= 180.000000'), nullable=False, default=1.0)
    workId = Column(Integer, ForeignKey('workList.workId'), nullable=False)
    victimId = Column(String(100), ForeignKey('work.workerId'), nullable=False)
    category = Column(String(8), nullable=False)


# 처리 상황 테이블
class AccidentProcessing(Base):
    __tablename__ = 'accidentProcessing'
    no = Column(Integer, ForeignKey('accident.no'),
                primary_key=True, autoincrement=True)
    situation = Column(String(100), nullable=True)
    date = Column(Date, nullable=True)
    time = Column(Time, nullable=True)
    detail = Column(String(100), nullable=True)
