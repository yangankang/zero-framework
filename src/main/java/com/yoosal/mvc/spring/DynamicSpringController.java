package com.yoosal.mvc.spring;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

@Component
public class DynamicSpringController extends SimpleUrlHandlerMapping {
    public static final String SPRING_CONTROLLER_NAME = "_MVCEntryPointController";
    private static Map<String, Object> mapping = new HashMap<String, Object>();

    public static void setMapping(String url, String key) {
        mapping.put(url, key);
    }

    public DynamicSpringController() {
        this.setOrder(Ordered.LOWEST_PRECEDENCE);
        if (mapping.size() > 0) {
            this.setUrlMap(mapping);
        }
    }


}
