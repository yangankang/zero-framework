package test.com.yoosal.mvc.model;

public class BeanModel {
    private int id;
    private String name;

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
}
