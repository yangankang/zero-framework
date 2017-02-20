package com.yoosal.mvc.support;

import com.yoosal.common.ClassUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.common.event.PublicEventContext;
import com.yoosal.json.JSON;
import com.yoosal.json.serializer.SerializerFeature;
import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.event.RequestEvent;
import com.yoosal.mvc.exception.SceneInvokeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractSceneSupport implements SceneSupport {
    private static final Emerge emerge = new DefaultEmerge();
    private ControllerMethodParse controllerClassSupport;
    private CatchFormat catchFormat = null;
    private PublicEventContext publicEventContext = null;

    public AbstractSceneSupport(ControllerMethodParse controllerClassSupport) {
        this.controllerClassSupport = controllerClassSupport;
        Class c = EntryPointManager.getCatchClass();
        if (c != null) {
            try {
                catchFormat = (CatchFormat) c.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object invoke() throws SceneInvokeException {
        RequestEvent requestEvent = null;
        if (publicEventContext != null) {
            requestEvent = new RequestEvent(publicEventContext);
            requestEvent.setBefore(true);
            requestEvent.setController(this.controllerClassSupport.getClazz().getSimpleName());
            requestEvent.setMethod(this.controllerClassSupport.getMethodName());
            Map penetrates = this.getPenetrate();
            if (penetrates != null) {
                requestEvent.setRequest((HttpServletRequest) this.getPenetrate().get(HttpServletRequest.class));
            }
            try {
                publicEventContext.fireCEvent(requestEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            //执行之前检查一下用户自定义的权限是否可执行
            AuthoritySupport authoritySupport = EntryPointManager.getAuthoritySupport();
            if (authoritySupport != null) {
                AuthorityReply authorityReply = new AuthorityReply(
                        this.controllerClassSupport.getClazz(),
                        this.controllerClassSupport.getControllerName(),
                        this.controllerClassSupport.getMethodName(),
                        this.controllerClassSupport.getInvokeName(),
                        this.controllerClassSupport.getInstance());


                authorityReply = authoritySupport.judge(authorityReply, this.getServletRequest(), this.getServletResponse());
                if (authorityReply != null && !authorityReply.isCanExecute()) {
                    return authorityReply.getMessage();
                }
            }
            //获得当前方法需要的参数值,通过request的获得String类型的参数进行转换类型
            Object[] objects = emerge.getAssignment(controllerClassSupport.getJavaMethodParamNames(), controllerClassSupport.getMethod(),
                    this.getParams(), this.getPenetrate());
            //开始执行当前方法
            return controllerClassSupport.getMethod().invoke(controllerClassSupport.getInstance(), objects);
        } catch (Exception e) {
            if (catchFormat != null) {
                return catchFormat.format(e);
            } else {
                String catchString = EntryPointManager.getCatchString();
                if (StringUtils.isNotBlank(catchString)) {
                    return catchString.replaceAll("[ex]", e.getCause().getClass().getSimpleName())
                            .replaceAll("[msg]", e.getMessage());
                } else {
                    throw new SceneInvokeException("invoke method failed", e);
                }
            }
        } finally {
            if (publicEventContext != null) {
                requestEvent.setBefore(false);
                try {
                    publicEventContext.fireCEvent(requestEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setControllerClassSupport(ControllerMethodParse controllerClassSupport) {
        this.controllerClassSupport = controllerClassSupport;
    }

    @Override
    public void setEventContext(PublicEventContext publicEventContext) {
        this.publicEventContext = publicEventContext;
    }

    @Override
    public ControllerMethodParse getControllerClassSupport() {
        return this.controllerClassSupport;
    }

    public abstract Map<String, String[]> getParams();

    public abstract Map<Class, Object> getPenetrate();

    public abstract HttpServletRequest getServletRequest();

    public abstract HttpServletResponse getServletResponse();

    @Override
    public String serialize(Object object) {
        if (ClassUtils.isPrimitiveOrWrapper(object.getClass()) || object.getClass().isAssignableFrom(String.class)) {
            return object == null ? "" : (String) object;
        } else {
            return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        }
    }
}
