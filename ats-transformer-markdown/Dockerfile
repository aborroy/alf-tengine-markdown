# Image provides a container in which to run a transformer for the Alfresco Transform Service.

FROM alfresco/alfresco-base-java:11.0.1-openjdk-centos-7-6784d76a7b81

ENV JAVA_OPTS=""

# Set default user information
ARG GROUP_NAME=alfresco
ARG GROUP_ID=1001
ARG USER_NAME=alfte
ARG USER_ID=33004

COPY target/*.jar /usr/bin/

RUN groupadd -g ${GROUP_ID} ${GROUP_NAME} && \
    useradd -u ${USER_ID} -G ${GROUP_NAME} ${USER_NAME} && \
    chgrp -R ${GROUP_NAME} /usr/bin/*.jar

EXPOSE 8090

USER ${USER_NAME}

ENTRYPOINT java $JAVA_OPTS -jar /usr/bin/*.jar
