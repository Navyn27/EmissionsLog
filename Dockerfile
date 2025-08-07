FROM public.ecr.aws/amazoncorretto/amazoncorretto:21

WORKDIR /app

# Only copy the built JAR
COPY target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]