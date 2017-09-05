FROM openjdk:8-jdk-alpine
ADD target/todo-svc.jar todo-svc.jar

# Empty for now but be sure to understand the challenges with container resource restrictions and the JVM
ENV JAVA_OPTS=""

RUN addgroup todousr
RUN adduser -D -G todousr todousr
USER todousr

LABEL "com.aragost.service"="todo-svc"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /todo-svc.jar" ]
