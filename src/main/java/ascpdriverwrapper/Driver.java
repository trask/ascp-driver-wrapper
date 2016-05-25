package ascpdriverwrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * this ascp driver wrapper resolves two issues with the underlying ascp driver:
 *
 * 1. the underlying ascp driver does not read ascp properties from the url string, so this wrapper
 * reads ascp properties from the url string and adds them to the properties object
 *
 * 2. the underlying ascp driver throws NullPointerException in jdbcCompliant() method prior to
 * calling connect()
 */
public class Driver implements java.sql.Driver {

    private static final String[] ASCP_PROPERTIES =
            new String[] {"appId", "query", "folder", "object", "reason"};

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (Exception e) {
        }
    }

    private volatile java.sql.Driver delegate;

    public Connection connect(String url, Properties info) throws SQLException {
        try {
            delegate = (java.sql.Driver) Class.forName("com.cyberark.aim.v550.tomcat.ASCPDriver")
                    .newInstance();
        } catch (InstantiationException e) {
            throw new SQLException(e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        Parsed parsed = parse(url, info);
        return delegate.connect(parsed.getUrl(), parsed.getInfo());
    }

    public boolean acceptsURL(String url) throws SQLException {
        return true;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        if (delegate == null) {
            throw new SQLFeatureNotSupportedException();
        }
        return delegate.getParentLogger();
    }

    static Parsed parse(String url, Properties info) {
        String[] parts = url.split(";");
        StringBuilder sb = new StringBuilder();
        Properties props = new Properties();
        props.putAll(info);
        for (String part : parts) {
            boolean found = false;
            for (String ascpProperty : ASCP_PROPERTIES) {
                if (part.startsWith(ascpProperty + "=")) {
                    props.setProperty(ascpProperty, part.substring(ascpProperty.length() + 1));
                    break;
                }
            }
            if (!found) {
                if (sb.length() > 0) {
                    sb.append(';');
                }
                sb.append(part);
            }
        }
        return new Parsed(url, props);
    }

    static class Parsed {

        private final String url;
        private final Properties info;

        Parsed(String url, Properties info) {
            this.url = url;
            this.info = info;
        }

        String getUrl() {
            return url;
        }

        Properties getInfo() {
            return info;
        }
    }
}
