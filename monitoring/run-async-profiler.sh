#!/bin/bash

# --- 설정 ---
CONTAINER_NAME="danji-api"
PROFILE_DURATION=60
OUTPUT_FILE="memory-profile.html"

# --- 스크립트 실행 ---
docker exec "$CONTAINER_NAME" /bin/sh -c '
    if command -v microdnf > /dev/null; then
        microdnf install -y wget tar
    elif command -v dnf > /dev/null; then
        dnf install -y wget tar
    elif command -v apt-get > /dev/null; then
        apt-get update && apt-get install -y wget tar
    elif command -v apk > /dev/null; then
        apk update && apk add wget tar
    else
        echo "에러: 지원되는 패키지 매니저(microdnf, dnf, apt-get, apk)를 찾을 수 없습니다."
        exit 1
    fi

    echo ">> async-profiler 다운로드 및 압축 해제..."
    cd /tmp
    wget https://github.com/async-profiler/async-profiler/releases/download/v4.1/async-profiler-4.1-linux-x64.tar.gz -O async-profiler.tar.gz
    tar -xzf async-profiler.tar.gz
'

if [ $? -ne 0 ]; then
    echo ""
    echo "에러: 프로파일러 설치에 실패했습니다."
    exit 1
fi

echo ""
echo "프로파일링을 시작합니다..."
docker exec "$CONTAINER_NAME" /tmp/async-profiler-4.1-linux-x64/bin/asprof -e malloc -d "$PROFILE_DURATION" -f "/tmp/$OUTPUT_FILE" 1

echo ""
echo "프로파일링이 완료되었습니다. 결과 파일을 로컬로 복사합니다..."
docker cp "$CONTAINER_NAME:/tmp/$OUTPUT_FILE" "./$OUTPUT_FILE"

echo ""
echo "✅ 완료! 현재 폴더에 '$OUTPUT_FILE'이 생성되었습니다."
