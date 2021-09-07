FROM alpine:latest

ENV CRYPTOGRAPHY_DONT_BUILD_RUST=1
RUN apk add --update --no-cache \
    build-base \
    ca-certificates \
    curl \
    git \
    py3-cryptography \
    py3-pip \
    python3 \
    python3-dev \
    zip


WORKDIR /usr/app

COPY requirements.txt . 
RUN pip3 install -r requirements.txt

COPY . .

EXPOSE 80

CMD ["python3", "main.py"]
