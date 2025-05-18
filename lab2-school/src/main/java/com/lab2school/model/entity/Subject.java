package com.lab2school.model.entity;

import java.util.Objects;

public class Subject {
	private int id;
	private String name;
	private String description;

	public Subject() {
	}

	public Subject(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Subject(String name, String description) {
		this.name = name;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Subject{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Subject subject = (Subject) o;
		return id == subject.id && Objects.equals(name, subject.name)
				&& Objects.equals(description, subject.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description);
	}
}