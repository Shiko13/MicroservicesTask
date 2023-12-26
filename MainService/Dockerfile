FROM openjdk:17-alpine
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8080
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]