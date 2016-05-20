package com.yoosal.mvc.support;

import com.yoosal.common.StringUtils;
import com.yoosal.json.JSON;
import com.yoosal.json.JSONObject;
import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.exception.ParseTemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultJavaScriptMapping implements JavaScriptMapping {
    private List<ControllerMethodParse> methodParses;
    private String DEFAULT_TEMPLATE = "JavaScriptTemplate.js";

    @Override
    public String readTemplate() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(DEFAULT_TEMPLATE);
        return IOUtils.toString(inputStream);
    }

    @Override
    public String parseTemplate() throws ParseTemplateException {
        if (methodParses != null) {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
            try {
                scriptEngine.eval(this.readTemplate());
                if (scriptEngine instanceof Invocable) {
                    Invocable invoke = (Invocable) scriptEngine;
                    String jsTemplateString = (String) invoke.invokeFunction("executor", JSON.toJSONString(list2Info()));
                    return jsTemplateString;
                }
            } catch (ScriptException e) {
                throw new ParseTemplateException("execute script error", e);
            } catch (IOException e) {
                throw new ParseTemplateException("may be not template file", e);
            } catch (NoSuchMethodException e) {
                throw new ParseTemplateException("has no function name executor", e);
            }
        }
        return null;
    }

    private CInfo list2Info() {
        Map<String, List<CUnit>> map = new HashMap();
        for (ControllerMethodParse methodParse : methodParses) {
            List array = map.get(methodParse.getControllerName());
            if (array == null) {
                array = new ArrayList();
                map.put(methodParse.getControllerName(), array);
            }
            CUnit cUnit = new CUnit(methodParse);
            array.add(cUnit);
            map.put(methodParse.getControllerName(), array);
        }
        return new CInfo(map);
    }

    @Override
    public void setMethodParses(List<ControllerMethodParse> methodParses) {
        this.methodParses = methodParses;
    }

    @Override
    public void generateToFile(String path, boolean isCompress) throws IOException, ParseTemplateException {
        String js = this.parseTemplate();
        if (isCompress) {
            Writer writer = null;
            try {
                Class.forName("com.yahoo.platform.yui.compressor.JavaScriptCompressor");
                writer = new FileWriter(path);
                YuiCompressorUtils.compress(js, writer);
            } catch (ClassNotFoundException e) {
                FileUtils.writeStringToFile(new File(path), js);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } else {
            FileUtils.writeStringToFile(new File(path), js);
        }
    }

    @Override
    public void generateToFile(String path) throws ParseTemplateException, IOException {
        String js = this.parseTemplate();
        FileUtils.writeStringToFile(new File(path), js);
    }

    class CInfo {
        public String suffixName;
        public Map<String, List<CUnit>> cunits;

        public CInfo(Map<String, List<CUnit>> cunits) {
            this.suffixName = EntryPointManager.getApiPrefix();
            this.cunits = cunits;
        }
    }

    class CUnit {
        public String url = "";
        public String paramString;
        public String paramObject;
        public ControllerMethodParse controllerMethodParse;

        public CUnit(ControllerMethodParse controllerMethodParse) {
            this.url = EntryPointManager.getRequestUri();
            this.url = (this.url == null ? "" : this.url);
            this.controllerMethodParse = controllerMethodParse;
            this.createParam();
        }

        /**
         * 将要生成的js数据初始化，如果是Resful请求需要增加方法
         */
        private void createParam() {
            String[] strings = this.controllerMethodParse.getJavaMethodParamNames();
            paramString = StringUtils.arrayToDelimitedString(strings, ",");
            JSONObject object = new JSONObject();
            String classKey = EntryPointManager.getClassKey();
            String controllerName = this.controllerMethodParse.getControllerName();
            if (StringUtils.isNotBlank(EntryPointManager.getClassKey())) {
                object.put(classKey, controllerName);
            }
            String methodKey = EntryPointManager.getMethodKey();
            String methodName = this.controllerMethodParse.getMethodName();
            if (StringUtils.isNotBlank(EntryPointManager.getMethodKey())) {
                object.put(methodKey, methodName);
            }
            for (String s : strings) {
                object.put(s, s);
            }
            paramObject = object.toJSONString().replaceAll("\"", "")
                    .replaceAll(classKey + ":" + controllerName, classKey + ":\"" + controllerName + "\"")
                    .replaceAll(methodKey + ":" + methodName, methodKey + ":\"" + methodName + "\"");
        }
    }
}
