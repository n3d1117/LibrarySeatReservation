package startup;

import org.hibernate.dialect.PostgreSQL95Dialect;

import java.sql.Types;

/**
 * Custom PostgreSQL dialect allowing OTHER types as a response from the database.
 * This is needed to correctly parse the output of the TimescaleDB query used to setup the hypertable:
 *      >   SELECT create_hypertable('reservations', 'datetime')
 */
public class CustomPostgreSQLDialect extends PostgreSQL95Dialect {

    public CustomPostgreSQLDialect() {
        super();
        registerHibernateType(Types.OTHER, String.class.getName());
    }
}
