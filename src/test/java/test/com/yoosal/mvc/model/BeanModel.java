package test.com.yoosal.mvc.model;

public class BeanModel {
    private int id;
    private String name;
    private String uName;
    private String Age;
    private String SCore;
    private String schOol;

    static {
        System.out.println("aaa");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getSCore() {
        return SCore;
    }

    public void setSCore(String SCore) {
        this.SCore = SCore;
    }

    public String getSchOol() {
        return schOol;
    }

    public void setSchOol(String schOol) {
        this.schOol = schOol;
    }
}
