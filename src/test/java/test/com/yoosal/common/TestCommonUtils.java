package test.com.yoosal.common;

import com.yoosal.common.ClassUtils;
import com.yoosal.mvc.support.Emerge;
import org.junit.Test;

public class TestCommonUtils {

    @Test
    public void TestInterfaceImplement() {
        Class[] clazzs = ClassUtils.getAllInterfacesForClass(Emerge.class);
        for (Class clz : clazzs) {
            System.out.println(clz.getName());
        }
    }
}
