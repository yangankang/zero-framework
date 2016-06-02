package test.com.yoosal.orm;

import com.yoosal.json.JSON;
import com.yoosal.orm.ModelObject;
import org.junit.Test;

public class TestModelObject {

    @Test
    public void testmo() {
        ModelObject object = new ModelObject();
        object.put(A.B, "hello");
        System.out.println(JSON.toJSONString(object));
        System.out.println(object.get(A.B));
    }
}

enum A {
    B, C, D, E, F
}