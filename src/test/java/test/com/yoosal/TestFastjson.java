package test.com.yoosal;

import com.yoosal.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author yangankang
 */
public class TestFastjson {
    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("a", "aaa");
        object.put("b", "bbb");
        object.put("c", "ccc");
        String s = object.toJSONString();
        System.out.println(URLEncoder.encode(s));
        System.out.println(URLEncoder.encode(URLEncoder.encode(s)));
        JSONObject.parse(URLDecoder.decode(URLEncoder.encode(s)));
    }
}
