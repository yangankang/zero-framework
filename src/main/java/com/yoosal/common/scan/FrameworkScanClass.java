package com.yoosal.common.scan;

import java.lang.annotation.Annotation;
import java.util.List;

public interface FrameworkScanClass {
    List getScanClass(String packagePath, Class<? extends Annotation> annotationClass);

    List getScanClassAndInstance(String packagePath, Class<? extends Annotation> annotationClass) throws IllegalAccessException, InstantiationException;
}
