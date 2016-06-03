package test.com.yoosal.orm;

import com.yoosal.json.JSON;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OrmFactory;
import com.yoosal.orm.core.OrmSceneOperation;
import com.yoosal.orm.query.Query;
import org.junit.Test;
import test.com.yoosal.orm.table.TableStudent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TestOrm {

    @Test
    public void testSave() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        OrmSceneOperation sceneOperation = new OrmSceneOperation();
        sceneOperation.save(object);
    }

    @Test
    public void testList() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        OrmSceneOperation sceneOperation = new OrmSceneOperation();
        List<ModelObject> objects = sceneOperation.list(Query.query(TableStudent.class));
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testQuery() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        OrmSceneOperation sceneOperation = new OrmSceneOperation();
        ModelObject objects = sceneOperation.query(Query.query(TableStudent.class));
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testUpdate() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        OrmSceneOperation sceneOperation = new OrmSceneOperation();
        object = sceneOperation.save(object);

        System.out.println(object.getString(TableStudent.idColumn));

        ModelObject objects = sceneOperation.query(Query.query(TableStudent.class).id(object.getString(TableStudent.idColumn)));
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testRemove() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));
        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        OrmSceneOperation sceneOperation = new OrmSceneOperation();
        sceneOperation.save(object);

        sceneOperation.remove(Query.query(TableStudent.class).id(object.getInteger(TableStudent.idColumn)));

        System.out.println("delete id : " + object.getInteger(TableStudent.idColumn));
    }

    @Test
    public void testCount() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));
        OrmSceneOperation sceneOperation = new OrmSceneOperation();
        try {
            sceneOperation.begin();
            for (int i = 0; i < 100; i++) {
                ModelObject object = new ModelObject();
                object.setObjectClass(TableStudent.class);
                object.put(TableStudent.nameForAccount, "yak");
                object.put(TableStudent.age, 20);
                sceneOperation.save(object);
                sceneOperation.remove(Query.query(TableStudent.class).id(object.getInteger(TableStudent.idColumn)));
                long count = sceneOperation.count(Query.query(TableStudent.class));
                System.out.println(count);
            }
            sceneOperation.commit();
        } catch (Exception e) {
            sceneOperation.rollback();
        }
    }
}
