package ascpdriverwrapper;

import java.util.Properties;

import ascpdriverwrapper.Driver.Parsed;
import org.junit.Assert;
import org.junit.Test;

public class DriverTest {

    @Test
    public void test() {
        // given
        String url = "vendorDriver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
                + ";jdbc:sqlserver://abc"
                + ";databaseName=bcd"
                + ";appId=cde"
                + ";query=safe=def"
                + ";folder=efg"
                + ";object=fgh"
                + ";reason=ghi";
        Properties info = new Properties();
        info.setProperty("user", "xyz");
        // when
        Parsed parsed = Driver.parse(url, info);
        // then
        Assert.assertEquals(url, parsed.getUrl());
        Properties properties = new Properties();
        properties.setProperty("user", "xyz");
        properties.setProperty("appId", "cde");
        properties.setProperty("query", "safe=def");
        properties.setProperty("folder", "efg");
        properties.setProperty("object", "fgh");
        properties.setProperty("reason", "ghi");
        Assert.assertEquals(properties, parsed.getInfo());
    }
}
