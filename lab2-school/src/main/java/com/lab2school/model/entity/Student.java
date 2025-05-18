package com.lab2school.model.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Student {
	private int id;
	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;
	private int schoolId;
	private School school;

	public Student() {
	}

	public Student(int id, String firstName, String lastName, LocalDate dateOfBirth, int schoolId) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.schoolId = schoolId;
	}

	public Student(String firstName, String lastName, LocalDate dateOfBirth, int schoolId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.schoolId = schoolId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		if (this.schoolId != schoolId) {
			this.schoolId = schoolId;
			if (this.school != null && this.school.getId() != schoolId) {
				this.school = null;
			}
		}
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
		this.schoolId = (school != null) ? school.getId() : 0;
	}

	@Override
	public String toString() {
		return "Student{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\''
				+ ", dateOfBirth=" + dateOfBirth + ", schoolId=" + schoolId + ", schoolName="
				+ (school != null ? school.getName() : "N/A") + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Student student = (Student) o;
		return id == student.id && schoolId == student.schoolId && Objects.equals(firstName, student.firstName)
				&& Objects.equals(lastName, student.lastName) && Objects.equals(dateOfBirth, student.dateOfBirth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, dateOfBirth, schoolId);
	}
}