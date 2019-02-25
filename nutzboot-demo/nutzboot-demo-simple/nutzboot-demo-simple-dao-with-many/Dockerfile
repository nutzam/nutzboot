FROM azul/zulu-openjdk:8
MAINTAINER wendal <wendal1985@gmail.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/nb.jar"]
# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/nb.jar