from sqlalchemy import Column, Double, ForeignKey, Integer, String, Date, Time, Float
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.schema import CheckConstraint

Base = declarative_base()


# 회사 목록 테이블
class CompanyList(Base):
    __tablename__ = "company_list"

    company = Column(String(100), primary_key=True)

    rel_managers = relationship("UserManager", backref="company_manager")
    rel_employees = relationship("UserEmployee", backref="company_employee")
    rel_work_lists = relationship("WorkList", backref="company_work_list")


# 안전 관리자 회원 테이블
class UserManager(Base):
    __tablename__ = "user_manager"

    id = Column(String(100), primary_key=True)
    password = Column(String(100), nullable=False)
    name = Column(String(4), nullable=False)
    email = Column(String(100), nullable=False, unique=True)
    phone_no = Column(String(100), nullable=False)
    company = Column(String(100), ForeignKey(
        "company_list.company"), nullable=True)
    alert_token = Column(String(200), nullable=True)
    login_token = Column(String(200), nullable=True)

    rel_employee = relationship("Work_list", backref="manager_work_list")


# 작업 목록
class Work_list(Base):
    __tablename__ = "work_list"

    work_id = Column(String(100), primary_key=True)
    name = Column(String(100), nullable=False)
    company = Column(String(100), ForeignKey(
        "company_list.company"), nullable=False)
    start_date = Column(Date, nullable=False)
    end_date = Column(Date, nullable=True)
    manager_id = Column(String(100), ForeignKey(
        "user_manager.id"), nullable=False)

    rel_work = relationship("Work", backref="work_list_work")
    rel_accident = relationship("Accident", backref="work_list_accident")


# 근로자 테이블
class UserEmployee(Base):
    __tablename__ = "user_employee"

    id = Column(String(100), primary_key=True)
    password = Column(String(100), nullable=False)
    name = Column(String(4), nullable=False)
    email = Column(String(100), nullable=False, unique=True)
    phone_no = Column(String(20), nullable=False)
    company = Column(String(100), ForeignKey(
        "company_list.company"), nullable=True)
    alert_token = Column(String(200), nullable=True)
    login_token = Column(String(200), nullable=True)

    rel_work = relationship("Work", backref="user_employee_work")
    rel_employee_accident = relationship(
        "Accident", backref="user_employee_accidents")


# 작업 참가 내역
class Work(Base):
    __tablename__ = "work"

    work_id = Column(String(100), ForeignKey(
        "work_list.work_id"), primary_key=True)
    worker_id = Column(String(100), ForeignKey(
        "user_employee.id"), primary_key=True)


# 사고 발생 테이블
class Accident(Base):
    __tablename__ = "accident"

    no = Column(Integer, primary_key=True, autoincrement=True)
    date = Column(Date, nullable=False)
    time = Column(Time, nullable=False)
    latitude = Column(Double, nullable=False, default=1.0)
    longitude = Column(Double, nullable=False, default=1.0)
    __table_args__ = (
        CheckConstraint(
            "longitude >= -180.000000 AND longitude <= 180.000000", name="ck_longitude"),
        CheckConstraint(
            "latitude >= -90.000000 AND latitude <= 90.000000", name="ck_latitude")
    )
    work_id = Column(String(100), ForeignKey(
        "work_list.work_id"), nullable=False)
    victim_id = Column(String(100), ForeignKey(
        "user_employee.id"), nullable=False)
    category = Column(String(100), nullable=False)

    rel_victim_id = relationship(
        "AccidentProcessing", backref="accident_processing", uselist=False)


# 처리상황 테이블
class AccidentProcessing(Base):
    __tablename__ = "accident_processing"

    no = Column(Integer, ForeignKey("accident.no"),
                primary_key=True, autoincrement=True)
    situation = Column(String(100), nullable=True)
    date = Column(Date, nullable=True)
    time = Column(Time, nullable=True)
    detail = Column(String(100), nullable=True)
