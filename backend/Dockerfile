# Source: https://github.com/BrizziB/Docker-Test_DevEnv

FROM jboss/wildfly:24.0.0.Final

ENV WILDFLY_USER admin
ENV WILDFLY_PASS password
ENV JBOSS_CLI /opt/jboss/wildfly/bin/jboss-cli.sh
ENV DEPLOYMENT_DIR /opt/jboss/wildfly/standalone/deployments/

ENV DB_NAME sample
ENV DB_USER postgres
ENV DB_PASS postgres
ENV DB_HOST db
ENV DB_PORT 5432
ENV POSTGRESQL_VERSION 42.2.23

RUN echo "Building wildfly"
RUN echo "=> Adding administrator user"
RUN $JBOSS_HOME/bin/add-user.sh -u $WILDFLY_USER -p $WILDFLY_PASS --silent

RUN echo "=> Starting WildFly server" && \
      bash -c '$JBOSS_HOME/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Downloading PostgreSQL driver" && \
      curl -k --location --output ./postgresql-${POSTGRESQL_VERSION}.jar --url https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_VERSION}/postgresql-${POSTGRESQL_VERSION}.jar && \
    echo "=> Adding PostgreSQL module" && \
      $JBOSS_CLI --connect --command="module add --name=org.postgresql --resources=./postgresql-${POSTGRESQL_VERSION}.jar --dependencies=javax.api,javax.transaction.api" && \
    echo "=> Adding PostgreSQL driver" && \
      $JBOSS_CLI --connect --command="/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)" && \
    echo "=> Adding main Datasource" && \
      $JBOSS_CLI --connect --command="data-source add \
        --name=PostgresDS \
        --jndi-name=java:jboss/datasources/PostgresDS \
        --user-name=${DB_USER} \
        --password=${DB_PASS} \
        --driver-name=postgresql \
        --connection-url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
        --use-ccm=false \
        --blocking-timeout-wait-millis=5000 \
        --enabled=true" && \
    echo "=> Shutting down WildFly and Cleaning up" && \
      $JBOSS_CLI --connect --command=":shutdown" && \
      rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* && \
      rm -f ./*.jar

EXPOSE 8080 9990 5005

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug", "*:5005"]