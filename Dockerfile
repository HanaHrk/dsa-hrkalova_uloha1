FROM ghcr.io/maxotta/kiv-ds-docker:latest

RUN yum -y -q install python3

CMD /usr/bin/python3
FROM openjdk:11
COPY ./out/production/PriorityCommunication/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Main"]
