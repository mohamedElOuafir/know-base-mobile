FROM python:3.11-slim

WORKDIR /app

COPY modules.txt .

RUN pip install --no-cache-dir --retries 10 --default-timeout=200 -r modules.txt

COPY src ./src

EXPOSE 8081

