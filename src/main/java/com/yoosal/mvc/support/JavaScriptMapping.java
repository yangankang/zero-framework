package com.yoosal.mvc.support;

import com.yoosal.mvc.exception.ParseTemplateException;

import java.io.IOException;
import java.util.List;

/**
 * 将APIController的注解的所有类映射成JavaScript类，并生成一个js文件
 */
public interface JavaScriptMapping {
    String readTemplate() throws IOException;

    String parseTemplate() throws ParseTemplateException;

    void setMethodParses(List<ControllerMethodParse> methodParses);

    void generateToFile(boolean isCompress);

    void generateToFile();
}
