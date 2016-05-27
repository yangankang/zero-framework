package com.yoosal.common.scan;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public interface FrameworkScanClass {
    Set getScanClass(String packagePath, Class<? extends Annotation> annotationClass);

    Set getScanClassAndInstance(String packagePath, Class<? extends Annotation> annotationClass) throws IllegalAccessException, InstantiationException;
}
