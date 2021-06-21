FROM tiangolo/uwsgi-nginx-flask:python3.6-alpine3.7

RUN apk add --update make cmake gcc g++ gfortran
RUN apk add --update python py-pip python-dev   
RUN pip install cython
RUN pip install numpy

WORKDIR /app

COPY . .

RUN pip install -r requirements.txt