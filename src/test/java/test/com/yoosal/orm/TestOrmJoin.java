package test.com.yoosal.orm;

import com.yoosal.json.JSON;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OrmFactory;
import com.yoosal.orm.core.SessionOperationManager;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;
import org.junit.Test;
import test.com.yoosal.orm.table.TableClass;
import test.com.yoosal.orm.table.TableScore;
import test.com.yoosal.orm.table.TableStudent;
import test.com.yoosal.orm.table.TableStudentClass;

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

    private ModelObject classModelObject = ModelObject.instance(TableClass.class);
    private ModelObject scoreModelObject = ModelObject.instance(TableScore.class);
    private ModelObject studentModelObject = ModelObject.instance(TableStudent.class);
    private ModelObject studentClassModelObject = ModelObject.instance(TableStudentClass.class);

    @Test
    public void testJoin2Table() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));
        String[] classNames = new String[]{"语文", "数学", "英语"};
        for (String className : classNames) {
            ModelObject object = classModelObject.clone()
                    .fluentPut(TableClass.className, className);
            SessionOperationManager sceneOperation = new SessionOperationManager();
            sceneOperation.save(object);
        }
    }

    @Test
    public void testJoin2TableSelect() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        SessionOperationManager sceneOperation = new SessionOperationManager();
        ModelObject object = sceneOperation.query(Query.query(TableStudent.class).join(Join.join(TableScore.class).where(TableStudent.idColumn, TableScore.studentId)));
        System.out.println(object);
    }

    @Test
    public void testJoin2TableSelectByCenterTable() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        SessionOperationManager sceneOperation = new SessionOperationManager();
        List<ModelObject> object = sceneOperation.list(Query.query(TableStudentClass.class)
                .join(Join.where(TableClass.class, TableStudentClass.classId, TableClass.id))
                .join(Join.where(TableStudent.class, TableStudentClass.studentId, TableStudent.idColumn)));
        System.out.println(object.toString());
    }

    @Test
    public void testJoin2TableSelectLike() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        SessionOperationManager sceneOperation = new SessionOperationManager();
        ModelObject object = sceneOperation.query(Query.query(TableStudent.class).like(TableStudent.nameForAccount, "mt"));
        System.out.println(object);
    }

    @Test
    public void testJoin2TableSelectPage() throws IllegalAccessException, IOException, InstantiationException, SQLException, InvocationTargetException, ClassNotFoundException {
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));

        SessionOperationManager sceneOperation = new SessionOperationManager();
        List<ModelObject> object = sceneOperation.list(Query.query(TableStudent.class).limit(0, 32).orderByDesc(TableStudent.idColumn));
        System.out.println(object.size());
    }
}
