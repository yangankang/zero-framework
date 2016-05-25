package test.com.yoosal.mvc;

import com.yoosal.mvc.EntryPointServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class TestMVCServlet {
    public static void main(String[] args) throws Exception {
        Server server = new Server(9999);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = new ServletHolder(new EntryPointServlet());
        servletHolder.setInitParameter("frameworkConfigLocation", "classpath:mvc.properties");
        context.addServlet(servletHolder, "/invoke.do");
        server.start();
        server.join();
    }
}
