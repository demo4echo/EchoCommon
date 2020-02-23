FROM openjdk:8-jre-alpine

ARG SERVICE_NAME
ARG PRODUCT_NAME
ARG PRODUCT_VERSION
ARG PRODUCT_DESCRIPTION
ARG MAINTAINER_MAIL
ARG BUILD_DATE
ARG VCS_URL
ARG VCS_REF
ARG VENDOR
ARG SCHEMA_VERSION="1.0"

WORKDIR /opt/com.efrat/$SERVICE_NAME/$PRODUCT_NAME

COPY ./build/libs/$PRODUCT_NAME-$PRODUCT_VERSION.jar ./

RUN addgroup echogroup && \
	adduser -D -H echouser -G echogroup && \
	ln -s ./$PRODUCT_NAME-$PRODUCT_VERSION.jar ./app.jar

USER echouser

ENTRYPOINT ["java","-jar","./app.jar"]

#LABEL maintainer="tiran.meltser@efrat.com"
LABEL maintainer="$MAINTAINER_MAIL"

# Add label according to label schema (http://label-schema.org/rc1)
LABEL build-date=$BUILD_DATE
LABEL name=$PRODUCT_NAME
LABEL description=$PRODUCT_DESCRIPTION
LABEL vcs-url=$VCS_URL
LABEL vcs-ref=$VCF_REF
LABEL vendor=$VENDOR
LABEL version=$PRODUCT_VERSION
LABEL schema-version=$SCHEMA_VERSION