# zero-framework-2.1框架说明
写此框架的目的除了尝试用框架解决问题之外，还有一点就是厌倦了做WEB时的添删改查以及不停的拷贝，缺少了编程的乐趣。
框架分为两个部分MVC和ORM，两部分可以独立运行也可以依托于Spring运行。

ps:框架并没有经过烧脑的设计，源码很容易看也很容易修改，其中asm和json是使用开源项目，因为和框架粘性很强所以直接拿了源码，还有一些代码是Spring的工具类特别好用也就没重复造轮子了

编程思想说明：

MVC：经常做web的人都明白写ajax的痛苦，不断的拷贝修改，ZeroFramework#mvc则将java的类映射成为js的api，在系统启动的时候会生成一个js的api映射文件，你可以在项目中引用这个.js文件然后调用和java同名的类和方法还有传入相同的参数就可以得到返回值

ORM：除了不想写SQL外也为了获取数据方便，所有的对象表示都是ModelObject，添删改都是针对ModelObject对象的，查询做了特别处理，查询可以理解为左表（中心表）和右表的概念，所有的查询都是以左表为中心包括中间表，向外join查询（暂时只有left）然后获得ModelObject对象

demo可以在test.com.yoosal包中查看运行

#ZeroFramework#MVC的配置

**第一种程序启动方式：**

配置文件 mvc.properties :
mvc.request.uri=/invoke.do
mvc.scan.package=test.com.yoosal.mvc.apicontroller
 
 mvc.request.uri:请求的地址
 mvc.scan.package:扫描包名，只要包含@APIController的都会是映射成一个JS函数
 
 被扫描的类：
 
  @APIController
  public class TestApiControllerA {

      /**
       * 测试访问地址
       * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=hello
       *
       * @return
       */
      @Printer
      public String hello() {
          return "hello world";
      }
  
      /**
       * 测试访问地址
       * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=printer&name=ankang
       *
       * @param name
       * @return
       */
      @Printer
      public String printer(String name) {
          System.out.println("your name is : " + name);
          return name;
      }
  
      /**
       * 测试访问地址
       * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=bean&bean={id:10,name:"ankang"}
       *
       * @param bean
       * @return
       */
      @Printer
      public BeanModel bean(BeanModel bean) {
          System.out.println(JSONObject.toJSONString(bean));
          return bean;
      }
  
      /**
       * 测试地址
       * http://localhost:9999/invoke.do?_class=TestApiControllerA&_method=sendRedirect
       *
       * @return
       */
      @Printer
      public String sendRedirect() {
          return "redirect:http://www.google.com.hk";
      }
  }

启动方式：

  `Server server = new Server(9999);`
  `ServletContextHandler context = new ServletContextHandler();`
  `context.setContextPath("/");`
  `server.setHandler(context);`
  `ServletHolder servletHolder = new ServletHolder(new EntryPointServlet());`
  `servletHolder.setInitParameter("frameworkConfigLocation", "classpath:mvc.properties");`
  `context.addServlet(servletHolder, "/invoke.do");`
  `server.start();`
  `server.join();`
  
  
**第二种Spring配置方式**

Spring的bean配置：

    <bean class="com.yoosal.mvc.SpringEntryPointManager">
         <property name="requestUri" value="/invoke.do"/>
         <property name="scanPackage" value="test.com.yoosal.mvc.apicontroller"/>
         <property name="writePath" value="/js/api.js"/>
         <property name="authoritySupport" ref="authoritySupport"/>
     </bean>

  
  Controller的程序：
  
   @Controller
   public class SpringControllerTest {
   
       @RequestMapping("/invoke.do")
       public void doing(HttpServletRequest request, HttpServletResponse response) {
           try {
               EntryPointManager.getViewResolver().resolver(request, response);
           } catch (SceneInvokeException e) {
               e.printStackTrace();
           } catch (ViewResolverException e) {
               e.printStackTrace();
           }
       }
   }
   
   
#ZeroFramework#ORM的配置

映射表类：
`@Table
 public enum TableScore {
 
     @Column(key = true, strategy = AutoIncrementStrategy.class)
     id,
     @Column(type = String.class, length = 128)
     className,
     @Column(type = Integer.class)
     score,
     @Column(type = Integer.class)
     studentId,
     @Column(type = Integer.class)
     classId
 }
`

**第一种程序启动方式**

orm的配置文件orm_mapping.properties：

`orm.scan.package=test.com.yoosal.orm.table`
`orm.db.alter=true`
`orm.db.convert=H2U`
`orm.db.showSql=true`
`orm.ds.proxool.class=org.logicalcobwebs.proxool.ProxoolDataSource`
`orm.ds.proxool.driver=com.mysql.jdbc.Driver`
`orm.ds.proxool.driverUrl=jdbc:mysql://localhost:3306/yoosal?useUnicode=true&characterEncoding=utf8`
`orm.ds.proxool.user=root`
`orm.ds.proxool.password=123456`
`orm.ds.proxool.houseKeepingTestSql=show tables`
`orm.ds.proxool.prototypeCount=5`
`orm.ds.proxool.hourseKeepingSleepTime=90000`
`orm.ds.proxool.minimumConnectionCount=1500`
`orm.ds.proxool.maximumConnectionCount=1500`
`orm.ds.proxool.alias=db1`
`orm.ds.proxool.simultaneousBuildThrottle=1000`

启动代码：
`OrmFactory.properties(TestDBMapping.class.getResourceAsStream("/orm_mapping.properties"));`

一个添加实例，其他实例可以在test.com.yoosal.orm.TestOrm这个类中找到：
`ModelObject object = new ModelObject();`
`object.setObjectClass(TableStudent.class);`
`object.put(TableStudent.nameForAccount, "yak");`
`object.put(TableStudent.age, 20);`
`SessionOperationManager sceneOperation = new SessionOperationManager();`
`sceneOperation.save(object);`

**第二种Spring配置方式**

`<bean class="com.yoosal.orm.SpringOperationManager">`
    `<property name="scanPackage" value="test.com.yoosal.orm.table"/>`
    `<property name="dataSource" ref="dataSource"/>`
`</bean>`