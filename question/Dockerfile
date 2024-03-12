# Java 17 버전을 사용하는 OpenJDK 베이스 이미지
FROM openjdk:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY ./build/libs/*.jar /app/app.jar

# Java 17 버전을 사용하는 OpenJDK 베이스 이미지
FROM openjdk:17-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/app.jar /app/app.jar

# 8080 포트 오픈
EXPOSE 8080

# JAR 파일 실행
CMD ["java", "-jar", "app.jar", "-Duser.timezone=Asia/Seoul"]
