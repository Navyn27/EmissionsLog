# Use Amazon Corretto 21 from AWS ECR
FROM public.ecr.aws/amazoncorretto/amazoncorretto:21

# Set the working directory
WORKDIR /app

# Copy the built jar file into the container
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]