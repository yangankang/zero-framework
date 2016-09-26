package test.com.yoosal.orm;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OrmFactory;
import com.yoosal.orm.core.SessionOperationManager;
import com.yoosal.orm.transaction.Transaction;
import test.com.yoosal.orm.table.TableStudent2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TestORMThing {

    public static void main(String[] args) throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException, InterruptedException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping_thing.properties"));
        final SessionOperationManager sceneOperation = new SessionOperationManager();

        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add2(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add1 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }

        Thread.sleep(1000);
    }

    public static void add(SessionOperationManager sceneOperation) {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add2(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add1 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }

    }

    public static void add2(SessionOperationManager sceneOperation) {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add3(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add2 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public static void add3(SessionOperationManager sceneOperation) {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add4(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add3 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public static void add4(SessionOperationManager sceneOperation) {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add5(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add4 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public static void add5(SessionOperationManager sceneOperation) throws Exception {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add6(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add5 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public static void add6(SessionOperationManager sceneOperation) throws Exception {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            add7(sceneOperation);

            sceneOperation.save(object);
            transaction.commit();

            System.out.println("add6 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public static void add7(SessionOperationManager sceneOperation) throws Exception {
        String getName = "" + Math.random();
        ModelObject object = new ModelObject();
        Transaction transaction = sceneOperation.createTransaction();
        try {
            transaction.begin();
            object = new ModelObject();
            object.setObjectClass(TableStudent2.class);
            object.put(TableStudent2.nameForAccount, getName);
            object.put(TableStudent2.age, 20);

            sceneOperation.save(object);
            transaction.commit();
            System.out.println("add7 = " + getName);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }
}
