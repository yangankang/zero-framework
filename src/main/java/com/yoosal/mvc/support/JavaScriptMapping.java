package com.yoosal.mvc.support;

import com.yoosal.mvc.exception.ParseTemplateException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

/**
 * 将APIController的注解的所有类映射成JavaScript类，并生成一个js文件
 */
public interface JavaScriptMapping {
    String readTemplate() throws IOException;

    String parseTemplate() throws ParseTemplateException;

    void setMethodParses(List<ControllerMethodParse> methodParses);

    void generateToFile(String path, boolean isCompress) throws IOException, ParseTemplateException;

    void generateToFile(String path) throws ParseTemplateException, IOException;

    void generateToStream(Writer out, boolean isCompress) throws ParseTemplateException, IOException;

    void setAuthoritySupport(AuthoritySupport authoritySupport);

    void writeForDeveloper();
}
