package com.lab2school.model.service;

import com.lab2school.model.dao.StudentGradeDao;
import com.lab2school.model.entity.StudentGrade;

import java.util.ArrayList;
import java.util.List;

public class StudentGradeService {
	private StudentGradeDao studentGradeDao;

	public StudentGradeService() {
		this.studentGradeDao = new StudentGradeDao();
	}

	private List<Integer> parseAndValidateGradesString(String commaSeparatedGrades) {
		List<Integer> gradeValues = new ArrayList<>();
		if (commaSeparatedGrades == null || commaSeparatedGrades.trim().isEmpty()) {
			return gradeValues;
		}
		String[] gradeStrings = commaSeparatedGrades.split(",");
		for (String gradeStr : gradeStrings) {
			try {
				int grade = Integer.parseInt(gradeStr.trim());
				if (grade < 1 || grade > 12) {
					System.err.println("StudentGradeService: Некоректне значення оцінки '" + gradeStr.trim()
							+ "'. Оцінка має бути від 1 до 12.");
					return null;
				}
				gradeValues.add(grade);
			} catch (NumberFormatException e) {
				System.err.println(
						"StudentGradeService: Некоректний формат оцінки '" + gradeStr.trim() + "'. Очікується число.");
				return null;
			}
		}
		return gradeValues;
	}

	public boolean addGradesForRegistration(int registrationId, List<Integer> validGradeValues) {
		if (validGradeValues == null) {
			System.err.println("StudentGradeService: Передано невалідний список оцінок для додавання.");
			return false;
		}
		if (validGradeValues.isEmpty()) {
			return true;
		}

		for (int gradeValue : validGradeValues) {
			StudentGrade sg = new StudentGrade(registrationId, gradeValue);
			if (!studentGradeDao.addGrade(sg)) {
				System.err.println("StudentGradeService: Не вдалося додати оцінку " + gradeValue + " для реєстрації ID "
						+ registrationId + ". Можливо, знадобиться відкат частково доданих оцінок.");
				return false;
			}
		}
		return true;
	}

	public boolean updateGradesForRegistration(int registrationId, String commaSeparatedGrades) {
		List<Integer> newGradeValues = parseAndValidateGradesString(commaSeparatedGrades);

		if (newGradeValues == null) {
			if (!(commaSeparatedGrades == null || commaSeparatedGrades.trim().isEmpty())) {
				System.err.println("StudentGradeService: Нові оцінки невалідні. Старі оцінки НЕ були змінені.");
				return false;
			}
		}

		if (!studentGradeDao.deleteGradesByRegistrationId(registrationId)) {
			System.err.println(
					"StudentGradeService: Не вдалося видалити старі оцінки для реєстрації ID " + registrationId);
			return false;
		}
		return addGradesForRegistration(registrationId, newGradeValues);
	}

	public List<StudentGrade> getGradesByRegistrationId(int registrationId) {
		return studentGradeDao.getGradesByRegistrationId(registrationId);
	}

	public boolean addGradesFromString(int registrationId, String commaSeparatedGrades) {
		if (commaSeparatedGrades == null || commaSeparatedGrades.trim().isEmpty()) {
			return true;
		}
		List<Integer> gradeValues = parseAndValidateGradesString(commaSeparatedGrades);
		if (gradeValues == null) {
			return false;
		}
		return addGradesForRegistration(registrationId, gradeValues);
	}
}