FROM node:16
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME
CMD ["java", "-version"]
RUN apt-get install -y tcpdump
RUN apt-get install -y net-tools

EXPOSE 4000
ADD target/JavaPcapFileRun.jar JavaPcapFileRun.jar

ENTRYPOINT ["java","-jar","/JavaPcapFileRun.jar"]

