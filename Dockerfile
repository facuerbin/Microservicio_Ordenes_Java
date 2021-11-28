# Docker para desarrollo
FROM gradle:6.9.1-jdk8

ENV RABBIT_URL host.docker.internal
ENV MONGO_URL host.docker.internal
ENV AUTH_SERVICE_URL http://host.docker.internal:3000

WORKDIR /app
RUN curl -L https://github.com/facuerbin/Microservicio_Ordenes_Java/tarball/main | tar xz --strip=1
RUN gradle build fatJar

# Puerto de Order service
EXPOSE 3004

CMD gradle run