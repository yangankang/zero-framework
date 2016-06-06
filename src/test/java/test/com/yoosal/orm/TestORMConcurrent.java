package test.com.yoosal.orm;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OrmFactory;
import com.yoosal.orm.core.SessionOperationManager;
import com.yoosal.orm.query.Query;
import org.junit.Test;
import test.com.yoosal.orm.table.TableStudent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static test.com.yoosal.mvc.TestMVCConcurrent.doGet;

public class TestORMConcurrent {

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ts();
                    System.out.println("once");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 20000);
    }

    private static void ts() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(1000);
        List<Callable<String>> callables = new LinkedList();
        OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));
        final SessionOperationManager sceneOperation = new SessionOperationManager();
        for (int i = 0; i < 1000; i++) {
            Callable callable = new Callable() {
                @Override
                public String call() throws Exception {
                    String getName = "" + Math.random();
                    sceneOperation.begin();
                    ModelObject object = new ModelObject();
                    object.setObjectClass(TableStudent.class);
                    object.put(TableStudent.nameForAccount, getName);
                    object.put(TableStudent.age, 20);
                    sceneOperation.save(object);
                    sceneOperation.commit();

                    ModelObject getObj = sceneOperation.query(Query.query(TableStudent.class).id(object.get(TableStudent.idColumn)));
                    if (getName.equals(getObj.getString(TableStudent.nameForAccount))) {
                    } else {
                        throw new Exception("not equal");
                    }
                    return getName;
                }
            };
            callables.add(callable);
        }
        List<Future<String>> results = service.invokeAll(callables);
        String name = null;
        for (Future<String> future : results) {
            String cname = future.get();
        }
        service.shutdown();

    }
}
