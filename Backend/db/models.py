from db.db_connection import Base
from sqlalchemy import CheckConstraint, Column, BigInteger, VARCHAR, Time, Float, ForeignKey, Date
from sqlalchemy.orm import relationship


# 회사 목록 테이블
class CompanyList(Base):
    __tablename__ = "company_list"
    company = Column(VARCHAR(length=100), primary_key=True)

    rel_manager = relationship("UserManager", backref="company_manager")
    rel_employee = relationship("UserEmployee", backref="company_employee")
    rel_work_list = relationship("Work_list", backref="company_work_list")


# 안전 관리자 회원 테이블
class UserManager(Base):
    __tablename__ = "user_manager"

    id = Column(VARCHAR(length=100), primary_key=True)
    password = Column(VARCHAR(length=100), nullable=False)
    name = Column(VARCHAR(length=4), nullable=False)
    email = Column(VARCHAR(length=100), nullable=True)
    company = Column(VARCHAR(length=100), ForeignKey(
        'company_list.company'), nullable=False)

    rel_employee = relationship("Work_list", backref="manager_work_list")


# 근로자 테이블
class UserEmployee(Base):
    __tablename__ = "user_employee"

    id = Column(VARCHAR(length=100), primary_key=True)
    password = Column(VARCHAR(length=100), nullable=False)
    name = Column(VARCHAR(length=4), nullable=False)
    email = Column(VARCHAR(length=100), nullable=True)
    helmet_no = Column(VARCHAR(length=100), nullable=True)
    phone_no = Column(VARCHAR(length=100), nullable=False)
    company = Column(VARCHAR(length=100), ForeignKey(
        'company_list.company'), nullable=False)

    rel_accident = relationship("Accident", backref="victim_employee")
    rel_work = relationship("Work", backref='work_employee')


# 사고 발생 테이블
class Accident(Base):
    __tablename__ = "accident"

    no = Column(BigInteger, primary_key=True, autoincrement=True)
    date = Column(Date, nullable=False)
    time = Column(Time, nullable=False)
    latitude = Column(Float(precision=6), nullable=False, default=1.0)
    longtitude = Column(Float(precision=6), nullable=False, default=1.0)
    __table_args__ = (
        CheckConstraint(
            'longitude >= -180.000000 AND longitude <= 180.000000', name='ck_longitude'),
        CheckConstraint(
            'latitude >= -90.000000 AND latitude <= 90.000000', name='ck_latitude')
    )
    victim_id = Column(VARCHAR(length=100), ForeignKey(
        'user_employee.id'), nullable=False)
    category = Column(VARCHAR(length=100), nullable=False)

    rel_victim_id = relationship(
        "AccidentProcessing", backref="accident_processing", uselist=False)


# 처리상황 테이블
class AccidentProcessing(Base):
    __tablename__ = "accident_processing"

    no = Column(BigInteger, ForeignKey('accident.no'),
                primary_key=True, autoincrement=True)
    situation = Column(VARCHAR(length=100), nullable=False)
    date = Column(Date, nullable=False)
    time = Column(Time, nullable=False)
    detail = Column(VARCHAR(length=100), nullable=False)


class Work_list(Base):
    __tablename__ = "work_list"

    name = Column(VARCHAR(length=100), primary_key=True)
    company = Column(VARCHAR(length=100), ForeignKey(
        'company_list.company'), nullable=False)
    start_date = Column(Date, nullable=False)
    end_date = Column(Date, nullable=True)
    manager = Column(VARCHAR(length=100), ForeignKey(
        'user_manager.id'), nullable=False)

    rel_work = relationship("Work", backref='work')


class Work(Base):
    __tablename__ = "work"

    name = Column(VARCHAR(length=100), ForeignKey(
        'work_list.name'), primary_key=True)
    id = Column(VARCHAR(length=100), ForeignKey(
        'user_employee.id'), primary_key=True)
