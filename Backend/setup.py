from setuptools import find_packages, setup

setup(
    name='headware-intelligence',
    version='0.1',
    packages=find_packages(where=['account', 'clustering', 'db', 'trend', 'weather'])
)