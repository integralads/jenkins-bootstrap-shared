#https://hub.docker.com/layers/library/openjdk/17-alpine/images/sha256-a996cdcc040704ec6badaf5fecf1e144c096e00231a29188596c784bcf858d05?context=explore
FROM nexus.303net.net:8443/openjdk:17-alpine

ADD build/distributions/*.tar /usr/

ARG JENKINS_HOME=/var/lib/jenkins

RUN set -ex; \
adduser -u 100 -G nogroup -h ${JENKINS_HOME} -S jenkins; \
apk add --no-cache --allow-untrusted font-dejavu-sans-mono-nerd --repository=https://dl-cdn.alpinelinux.org/alpine/edge/community; \
apk add --no-cache bash fontconfig git openssh rsync; \
apk add --no-cache --update python3 py3-pip; \
mkdir -p /var/cache/jenkins ${JENKINS_HOME}; \
chown -R jenkins: /usr/lib/jenkins /var/cache/jenkins ${JENKINS_HOME}; \
ln -s /usr/lib/jenkins/distrib/daemon/run.sh /run.sh

EXPOSE 8080/tcp

USER jenkins
WORKDIR ${JENKINS_HOME}
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
CMD /run.sh
