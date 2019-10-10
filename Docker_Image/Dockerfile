# Use an official Python runtime as a parent image
FROM tomcat:9.0.12-jre8-slim

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Move crypto files
RUN mkdir /app/crypto
RUN mv /app/pubKey_bootstrappable.txt /app/crypto/pubKey_bootstrappable.txt
RUN mv /app/context_bootstrappable.txt /app/crypto/context_bootstrappable.txt

# Install any needed packages specified in requirements.txt
RUN apt-get update && apt-get install -y \
	build-essential \
	maven \
	libcpprest-dev \
	openjdk-8-jdk \
	libarmadillo-dev

# Make port 8080 available to the world outside this container
EXPOSE 8080

ENV JAVA_HOME /docker-java-home
ENV LD_LIBRARY_PATH $LD_LIBRARY_PATH:/usr/local/lib

# Postgres JDBC Driver
RUN mv /app/postgresql-42.2.5.jar /usr/local/tomcat/lib/

# Service Generation Servlet
RUN mv /app/serviceGenerator.war /usr/local/tomcat/webapps

# NTL
RUN mv /app/libntl.a /usr/local/lib/libntl.a
RUN mv /app/libntl.la /usr/local/lib/libntl.la
RUN mv /app/libntl.so.39 /usr/local/lib/libntl.so.39
RUN mv /app/libntl.so /usr/local/lib/libntl.so
RUN mv /app/libntl.so.39.0.1 /usr/local/lib/libntl.so.39.0.1
RUN mv /app/NTL /usr/local/include/

# GMP
RUN mv /app/libgmp.a /usr/local/lib/libgmp.a
RUN mv /app/libgmp.so /usr/local/lib/libgmp.so
RUN mv /app/libgmp.so.10.3.2 /usr/local/lib/libgmp.so.10.3.2
RUN mv /app/libgmp.so.10 /usr/local/lib/libgmp.so.10
RUN mv /app/gmp.h /usr/local/include

# LibSSH
RUN mv /app/libssh.so /usr/local/lib/libssh.so
RUN mv /app/libssh.so.4 /usr/local/lib/libssh.so.4
RUN mv /app/libssh.so.4.7.2 /usr/local/lib/libssh.so.4.7.2

#HElib
RUN mv /app/libfhe.a /usr/local/lib/libfhe.a
RUN mv /app/helib /usr/local/include/

# Run app.py when the container launches
CMD ["catalina.sh", "run"]