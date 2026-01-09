package app.model;

public class Student {
    private String student_no;
    private String name;
    private String department;

    public Student(String student_no, String name, String department) {
        this.student_no = student_no;
        this.name = name;
        this.department = department;
    }

    // Getter ve Setter'lar
    public String getStudent_no() { return student_no; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
}
