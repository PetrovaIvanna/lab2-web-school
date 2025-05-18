package com.lab2school.model.service;

import com.lab2school.model.dao.StudentDao;
import com.lab2school.model.dao.SchoolDao;
import com.lab2school.model.entity.Student;
import com.lab2school.model.entity.School;

import java.util.List;

public class StudentService {
	private StudentDao studentDao;
	private SchoolDao schoolDao;

	public StudentService() {
		this.studentDao = new StudentDao();
		this.schoolDao = new SchoolDao();
	}

	private String getStudentValidation(Student student, boolean isUpdateOperation) {
		if (student == null) {
			return "StudentService: Об'єкт студента не може бути null.";
		}
		if (isUpdateOperation && student.getId() <= 0) {
			return "StudentService: Некоректний ID студента (" + student.getId() + ") для оновлення.";
		}
		if (student.getFirstName() == null || student.getFirstName().trim().isEmpty() || student.getLastName() == null
				|| student.getLastName().trim().isEmpty()) {
			return "StudentService: Ім'я та прізвище студента не можуть бути порожніми.";
		}
		if (student.getSchoolId() <= 0) {
			return "StudentService: Некоректний ID школи (" + student.getSchoolId() + ") для студента.";
		}
		if (schoolDao.getSchoolById(student.getSchoolId()) == null) {
			return "StudentService: Школа з ID " + student.getSchoolId() + " не знайдена.";
		}
		return null;
	}

	public boolean addStudent(Student student) {
		String validationError = getStudentValidation(student, false);
		if (validationError != null) {
			System.err.println(validationError);
			return false;
		}
		return studentDao.addStudent(student);
	}

	public boolean updateStudent(Student student) {
		String validationError = getStudentValidation(student, true);
		if (validationError != null) {
			System.err.println(validationError);
			return false;
		}

		if (studentDao.getStudentById(student.getId()) == null) {
			System.err.println("StudentService: Студента з ID " + student.getId() + " не знайдено для оновлення.");
			return false;
		}

		return studentDao.updateStudent(student);
	}

	public boolean deleteStudent(int studentId) {
		if (studentId <= 0) {
			System.err.println("StudentService: Некоректний ID студента (" + studentId + ") для видалення.");
			return false;
		}
		if (studentDao.getStudentById(studentId) == null) {
			System.err.println("StudentService: Студента з ID " + studentId + " не знайдено для видалення.");
			return false;
		}
		return studentDao.deleteStudent(studentId);
	}

	public Student getStudentById(int studentId) {
		Student student = studentDao.getStudentById(studentId);
		if (student != null && student.getSchoolId() != 0 && student.getSchool() == null) {
			School school = schoolDao.getSchoolById(student.getSchoolId());
			student.setSchool(school);
		}
		return student;
	}

	public List<Student> getAllStudents() {
		List<Student> students = studentDao.getAllStudents();
		for (Student student : students) {
			if (student.getSchoolId() != 0 && student.getSchool() == null) {
				School school = schoolDao.getSchoolById(student.getSchoolId());
				student.setSchool(school);
			}
		}
		return students;
	}
}