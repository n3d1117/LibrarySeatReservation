package startup;

import org.hibernate.dialect.PostgreSQL95Dialect;

import java.sql.Types;

public class CustomPostgreSQLDialect extends PostgreSQL95Dialect {

    public CustomPostgreSQLDialect() {
        super();
        registerHibernateType(Types.OTHER, String.class.getName());
    }
}
