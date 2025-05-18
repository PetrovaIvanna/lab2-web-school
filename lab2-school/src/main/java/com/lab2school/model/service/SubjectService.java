package com.lab2school.model.service;

import com.lab2school.model.dao.SubjectDao;
import com.lab2school.model.entity.Subject;

import java.util.List;

public class SubjectService {
	private SubjectDao subjectDao;

	public SubjectService() {
		this.subjectDao = new SubjectDao();
	}

	private String getSubjectValidationError(Subject subject, boolean isUpdateOperation) {
		if (subject == null) {
			return "SubjectService: Об'єкт предмету не може бути null.";
		}
		if (isUpdateOperation && subject.getId() <= 0) {
			return "SubjectService: Некоректний ID предмету (" + subject.getId() + ") для оновлення.";
		}
		if (subject.getName() == null || subject.getName().trim().isEmpty()) {
			return "SubjectService: Назва предмету не може бути порожньою.";
		}
		return null;
	}

	public boolean addSubject(Subject subject) {
		String validationError = getSubjectValidationError(subject, false);
		if (validationError != null) {
			System.err.println(validationError);
			return false;
		}
		return subjectDao.addSubject(subject);
	}

	public boolean updateSubject(Subject subject) {
		String validationError = getSubjectValidationError(subject, true);
		if (validationError != null) {
			System.err.println(validationError);
			return false;
		}

		if (subjectDao.getSubjectById(subject.getId()) == null) {
			System.err.println("SubjectService: Предмет з ID " + subject.getId() + " не знайдено для оновлення.");
			return false;
		}
		return subjectDao.updateSubject(subject);
	}

	public boolean deleteSubject(int subjectId) {
		if (subjectId <= 0) {
			System.err.println("SubjectService: Некоректний ID предмету (" + subjectId + ") для видалення.");
			return false;
		}
		if (subjectDao.getSubjectById(subjectId) == null) {
			System.err.println("SubjectService: Предмет з ID " + subjectId + " не знайдено для видалення.");
			return false;
		}
		return subjectDao.deleteSubject(subjectId);
	}

	public Subject getSubjectById(int subjectId) {
		return subjectDao.getSubjectById(subjectId);
	}

	public List<Subject> getAllSubjects() {
		return subjectDao.getAllSubjects();
	}
}