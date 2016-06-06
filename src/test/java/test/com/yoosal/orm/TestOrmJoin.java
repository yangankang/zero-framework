package test.com.yoosal.orm;

import com.yoosal.json.JSON;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OrmFactory;
import com.yoosal.orm.core.SessionOperationManager;
import com.yoosal.orm.query.Query;
import org.junit.Test;
import test.com.yoosal.orm.table.TableStudent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestOrmJoin {

    @Test
    public void testMapping() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));
    }

    @Test
    public void testJoin2Table() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        SessionOperationManager sceneOperation = new SessionOperationManager();
        sceneOperation.save(object);
    }
}
