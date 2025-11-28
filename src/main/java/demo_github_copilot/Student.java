package demo_github_copilot;
/**
 * Tạo class Student có các thuộc tính id, name, age, class, score
 * và các hành vi (methods) read, write, study
 * có constructor, getter, setter
 */
public class Student {
    private int id;
    private String name;
    private int age;
    private String className;
    private float score;

    public Student(int id, String name, int age, String className, float score) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.className = className;
        this.score = score;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void read() {
        System.out.println(name + " is reading.");
    }

    public void write() {
        System.out.println(name + " is writing.");
    }

    public void study() {
        System.out.println(name + " is studying.");
    }
}
