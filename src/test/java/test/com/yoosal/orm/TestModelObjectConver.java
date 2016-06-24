package test.com.yoosal.orm;

import com.yoosal.orm.ModelObject;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class TestModelObjectConver {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(1000);
        List<Callable<Integer>> callables = new LinkedList();
        for (int i = 0; i < 1000000; i++) {
            Callable callable = new Callable() {
                @Override
                public Integer call() throws Exception {
                    ModelObject object = new ModelObject();
                    int rnd = (int) (Math.random() * 10);
                    if (rnd > 5) {
                        object.put("key", "2016-06-17");
                        object.convert("key", Date.class);
                    } else {
                        object.put("key", "20");
                        object.convert("key", int.class);
                    }
                    return 1;
                }
            };
            callables.add(callable);
        }
        List<Future<Integer>> results = service.invokeAll(callables);
        String c = null;
        for (Future<Integer> future : results) {
            Integer ci = future.get();
        }
        service.shutdown();
    }


    @Test
    public void testDate() {
        ModelObject object = new ModelObject();
        object.put("date", new Date());
        System.out.println(object);
    }
}
