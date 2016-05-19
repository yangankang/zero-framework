package com.yoosal.mvc.support;

import com.yoosal.common.ClassUtils;
import com.yoosal.json.JSON;
import com.yoosal.mvc.exception.SceneInvokeException;

import java.util.Map;

public abstract class AbstractSceneSupport implements SceneSupport {
    private static final Emerge emerge = new DefaultEmerge();
    private ControllerClassSupport controllerClassSupport;

    public AbstractSceneSupport(ControllerClassSupport controllerClassSupport) {
        this.controllerClassSupport = controllerClassSupport;
    }

    @Override
    public Object invoke() throws SceneInvokeException {
        try {
            //获得当前方法需要的参数值
            Object[] objects = emerge.getAssignment(controllerClassSupport.getJavaMethodParamNames(), controllerClassSupport.getMethod(),
                    this.getParams(), this.getPenetrate());
            //开始执行当前方法
            return controllerClassSupport.getMethod().invoke(controllerClassSupport.getInstance(), objects);
        } catch (Exception e) {
            throw new SceneInvokeException("invoke method failed", e);
        }
    }

    @Override
    public void setControllerClassSupport(ControllerClassSupport controllerClassSupport) {
        this.controllerClassSupport = controllerClassSupport;
    }

    @Override
    public ControllerClassSupport getControllerClassSupport() {
        return this.controllerClassSupport;
    }

    public abstract Map<String, String[]> getParams();

    public abstract Map<Class, Object> getPenetrate();

    @Override
    public String serialize(Object object) {
        if (ClassUtils.isPrimitiveOrWrapper(object.getClass()) || object.getClass().isAssignableFrom(String.class)) {
            return String.valueOf(object);
        } else {
            return JSON.toJSONString(object);
        }
    }
}
