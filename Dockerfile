FROM openjdk:8-jre-alpine

ARG JAR_PRODUCT_NAME
ARG JAR_PRODUCT_VERSION

WORKDIR /opt/com.mavenir/demo-echo/$JAR_PRODUCT_NAME

COPY ./build/libs/$JAR_PRODUCT_NAME-$JAR_PRODUCT_VERSION.jar ./

RUN addgroup echogroup && \
	adduser -D -H echouser -G echogroup && \
	ln -s ./$JAR_PRODUCT_NAME-$JAR_PRODUCT_VERSION.jar ./app.jar

USER echouser

ENTRYPOINT ["java","-jar","./app.jar"]

LABEL maintainer="tiran.meltser@efrat.com"
