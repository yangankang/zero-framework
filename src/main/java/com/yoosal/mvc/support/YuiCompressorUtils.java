package com.yoosal.mvc.support;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public abstract class YuiCompressorUtils {
    public static void compress(String code, Writer writer) throws IOException {
        Reader in = new InputStreamReader(IOUtils.toInputStream(code));
        JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
            public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    System.err.println("/n[WARNING] " + message);
                } else {
                    System.err.println("/n[WARNING] " + line + ':' + lineOffset + ':' + message);
                }
            }

            public void error(String message, String sourceName,
                              int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    System.err.println("/n[ERROR] " + message);
                } else {
                    System.err.println("/n[ERROR] " + line + ':' + lineOffset + ':' + message);
                }
            }

            public EvaluatorException runtimeError(String message, String sourceName,
                                                   int line, String lineSource, int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
        });
        compressor.compress(writer, -1, true, false, false, false);
    }
}
