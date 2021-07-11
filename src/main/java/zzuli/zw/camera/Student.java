package zzuli.zw.camera;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @ClassName Student
 * @Description TODO
 * @Author wu2wen
 * @Date 2021/7/9 14:56
 * @Version 1.0
 */
public class Student implements Serializable {
    private String name;
    private String student;
    private int id;
    private String grade;
    private String image;
    private byte[] feature;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", student='" + student + '\'' +
                ", id=" + id +
                ", grade='" + grade + '\'' +
                ", image='" + image + '\'' +
                ", feature=" + Arrays.toString(feature) +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
