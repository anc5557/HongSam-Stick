# Java 17 버전을 사용하는 OpenJDK 베이스 이미지
FROM openjdk:17-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY ./build/libs/question-0.0.1-SNAPSHOT.jar /app/app.jar

# 8080 포트 오픈
EXPOSE 8080

# JAR 파일 실행
CMD ["java", "-jar", "app.jar", "-Duser.timezone=Asia/Seoul"]
