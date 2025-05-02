# Build stage

#FROM azul/zulu-openjdk:17.0.10 AS builder
#
#WORKDIR /app
#
#COPY . .
#
#RUN chmod +x ./gradlew
#RUN ./gradlew clean build -x test
#
## Run stage
#
#FROM azul/zulu-openjdk:17.0.10-jre
#
#WORKDIR /app
#
#COPY --from=builder /app/build/libs/*.jar map.jar
#
#EXPOSE 18080
#
#ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","map.jar"]

# Build stage
FROM azul/zulu-openjdk:17.0.10 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test
# 실제 생성된 JAR 파일 확인
RUN find /app -name "*.jar" | xargs ls -la

# Run stage
FROM azul/zulu-openjdk:17.0.10-jre
WORKDIR /app

# 명확한 이름으로 JAR 파일 복사 (빌드 시 생성된 실제 이름을 사용)
COPY --from=builder /app/build/libs/*.jar /app/map.jar

EXPOSE 18080
# JAR 파일 실행 전 존재 확인
RUN ls -la /app
ENTRYPOINT ["java", "-Dspring.profiles.active=prod","-jar","/app/map.jar"]