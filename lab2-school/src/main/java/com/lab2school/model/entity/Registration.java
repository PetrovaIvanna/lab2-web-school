package com.lab2school.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Registration {
    private int id;
    private int studentId;
    private int subjectId;

    private Student student;
    private Subject subject;
    private List<StudentGrade> studentGrades;

    public Registration() {
        this.studentGrades = new ArrayList<>();
    }

    public Registration(int id, int studentId, int subjectId) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.studentGrades = new ArrayList<>();
    }

    public Registration(int studentId, int subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.studentGrades = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<StudentGrade> getStudentGrades() {
        return studentGrades;
    }

    public void setStudentGrades(List<StudentGrade> studentGrades) {
        this.studentGrades = studentGrades;
    }

    @Override
    public String toString() {
        return "Registration{" +
               "id=" + id +
               ", studentId=" + studentId +
               ", subjectId=" + subjectId +
               ", student=" + (student != null ? student.getFirstName() + " " + student.getLastName() : "null") +
               ", subject=" + (subject != null ? subject.getName() : "null") +
               ", studentGrades=" + studentGrades +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return id == that.id &&
               studentId == that.studentId &&
               subjectId == that.subjectId &&
               Objects.equals(studentGrades, that.studentGrades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, subjectId, studentGrades);
    }
}