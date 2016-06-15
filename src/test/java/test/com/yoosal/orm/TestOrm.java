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
import java.sql.*;
import java.util.List;

public class TestOrm {

    @Test
    public void testSave() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_normal.properties"));

        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        SessionOperationManager sceneOperation = new SessionOperationManager();
        sceneOperation.save(object);
    }

    @Test
    public void testList() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_normal.properties"));

        SessionOperationManager sceneOperation = new SessionOperationManager();
        List<ModelObject> objects = sceneOperation.list(Query.query(TableStudent.class));
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testQuery() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_normal.properties"));

        SessionOperationManager sceneOperation = new SessionOperationManager();
        ModelObject objects = sceneOperation.query(Query.query(TableStudent.class));
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testUpdate() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_normal.properties"));

        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        SessionOperationManager sceneOperation = new SessionOperationManager();
        object = sceneOperation.save(object);

        System.out.println(object.getString(TableStudent.idColumn));

        ModelObject objects = sceneOperation.query(Query.query(TableStudent.class).id(object.getString(TableStudent.idColumn)));
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testRemove() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_normal.properties"));
        ModelObject object = new ModelObject();
        object.setObjectClass(TableStudent.class);
        object.put(TableStudent.nameForAccount, "yak");
        object.put(TableStudent.age, 20);

        SessionOperationManager sceneOperation = new SessionOperationManager();
        sceneOperation.save(object);

        sceneOperation.remove(Query.query(TableStudent.class).id(object.getInteger(TableStudent.idColumn)));

        System.out.println("delete id : " + object.getInteger(TableStudent.idColumn));
    }

    @Test
    public void testCount() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_normal.properties"));
        SessionOperationManager sceneOperation = new SessionOperationManager();
        try {
//            sceneOperation.begin();
            long time = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                ModelObject object = new ModelObject();
                object.setObjectClass(TableStudent.class);
                object.put(TableStudent.nameForAccount, "yak");
                object.put(TableStudent.age, 20);
                sceneOperation.save(object);
            }
            /*sceneOperation.remove(Query.query(TableStudent.class).id(object.getInteger(TableStudent.idColumn)));
            long count = sceneOperation.count(Query.query(TableStudent.class));
            System.out.println(count);*/
            long endTime = System.currentTimeMillis();
            System.out.println((endTime - time) / 1000);
//            sceneOperation.commit();
        } catch (Exception e) {
//            sceneOperation.rollback();
        }
    }

    @Test
    public void testJDBC() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/yoosal?useUnicode=true&characterEncoding=utf8";
        String username = "root";
        String password = "123456";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = DriverManager.getConnection(url, username, password);
            String sql = "insert into table_student (name_for_account,age) values('yak',20)";
            Statement pstmt;
            long time = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                pstmt = conn.createStatement();
                /*pstmt.setString(1, "yak");
                pstmt.setInt(2, 20);
                pstmt.executeUpdate();*/
                pstmt.execute(sql);
                pstmt.close();
            }
            long endTime = System.currentTimeMillis();
            System.out.println((endTime - time) / 1000);
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
