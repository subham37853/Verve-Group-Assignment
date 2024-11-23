FROM openjdk:17-jdk-slim
ENV JAVA_OPTS="-Xms1g -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/dump"
WORKDIR /app
COPY build/libs/your-application.jar /app/application.jar
EXPOSE 9091
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]
