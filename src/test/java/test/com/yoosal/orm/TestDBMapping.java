package test.com.yoosal.orm;

import com.yoosal.orm.OrmFactory;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TestDBMapping {

    @Test
    public void testStart() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));
    }
}
