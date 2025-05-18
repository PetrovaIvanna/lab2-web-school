package com.lab2school.model.entity;

import java.util.Objects;

public class StudentGrade {
	private int id;
	private int registrationId;
	private int gradeValue;

	public StudentGrade() {
	}

	public StudentGrade(int registrationId, int gradeValue) {
		this.registrationId = registrationId;
		this.gradeValue = gradeValue;
	}

	public StudentGrade(int id, int registrationId, int gradeValue) {
		this.id = id;
		this.registrationId = registrationId;
		this.gradeValue = gradeValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}

	public int getGradeValue() {
		return gradeValue;
	}

	public void setGradeValue(int gradeValue) {
		this.gradeValue = gradeValue;
	}

	@Override
	public String toString() {
		return "StudentGrade{" + "id=" + id + ", registrationId=" + registrationId + ", gradeValue=" + gradeValue + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StudentGrade that = (StudentGrade) o;
		return id == that.id && registrationId == that.registrationId && gradeValue == that.gradeValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, registrationId, gradeValue);
	}
}