#https://hub.docker.com/layers/library/alpine/3.17.3/images/sha256-b6ca290b6b4cdcca5b3db3ffa338ee0285c11744b4a6abaa9627746ee3291d8d?context=explore
ARG base=nexus.303net.net:8443/alpine:3.17.3
FROM ${base}

ADD build/distributions/*.tar /usr/

ARG JENKINS_HOME=/var/lib/jenkins

RUN set -ex; \
adduser -u 100 -G nogroup -h ${JENKINS_HOME} -S jenkins; \
mkdir -p /var/cache/jenkins ${JENKINS_HOME}; \
chown -R jenkins: /usr/lib/jenkins /var/cache/jenkins ${JENKINS_HOME}; \
ln -s /usr/lib/jenkins/distrib/daemon/run.sh /run.sh

RUN set -ex; \
apk add --no-cache bash fontconfig git openssh rsync openjdk17-jdk font-dejavu-sans-mono-nerd; \
apk add --no-cache --update python3

EXPOSE 8080/tcp

USER jenkins
WORKDIR ${JENKINS_HOME}
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
CMD /run.sh
