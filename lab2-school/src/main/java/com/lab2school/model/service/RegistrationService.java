package com.lab2school.model.service;

import com.lab2school.model.dao.RegistrationDao;
import com.lab2school.model.dao.StudentDao;
import com.lab2school.model.dao.SubjectDao;
import com.lab2school.model.entity.Registration;
import com.lab2school.model.entity.Student;
import com.lab2school.model.entity.Subject;

import java.util.List;

public class RegistrationService {
	private RegistrationDao registrationDao;
	private StudentDao studentDao;
	private SubjectDao subjectDao;
	private StudentGradeService studentGradeService;

	public RegistrationService() {
		this.registrationDao = new RegistrationDao();
		this.studentDao = new StudentDao();
		this.subjectDao = new SubjectDao();
		this.studentGradeService = new StudentGradeService();
	}

	public boolean addRegistrationWithGrades(Registration registration, String commaSeparatedGrades) {
		Student student = studentDao.getStudentById(registration.getStudentId());
		if (student == null) {
			System.err.println("RegistrationService: Студент з ID " + registration.getStudentId() + " не знайдений.");
			return false;
		}
		Subject subject = subjectDao.getSubjectById(registration.getSubjectId());
		if (subject == null) {
			System.err.println("RegistrationService: Предмет з ID " + registration.getSubjectId() + " не знайдений.");
			return false;
		}

		if (!registrationDao.addRegistration(registration)) {
			System.err.println("RegistrationService: Не вдалося створити основний запис реєстрації.");
			return false;
		}

		if (registration.getId() > 0) {
			if (!studentGradeService.addGradesFromString(registration.getId(), commaSeparatedGrades)) {
				System.err.println("RegistrationService: Помилка при додаванні оцінок для реєстрації ID "
						+ registration.getId() + ". Відкочуємо створення реєстрації.");
				if (!registrationDao.deleteRegistration(registration.getId())) {
					System.err.println("RegistrationService: ПОМИЛКА ПРИ ВІДКАТІ! Не вдалося видалити реєстрацію ID "
							+ registration.getId());
				}
				return false;
			}
		} else {
			System.err.println("RegistrationService: Не вдалося отримати ID для нової реєстрації, оцінки не додані.");
			return false;
		}

		return true;
	}

	public Registration getRegistrationById(int registrationId) {
		return registrationDao.getRegistrationById(registrationId);
	}

	public List<Registration> getAllRegistrations() {
		return registrationDao.getAllRegistrations();
	}

	public boolean updateRegistrationWithGrades(Registration registration, String commaSeparatedGrades) {
		Registration existingReg = registrationDao.getRegistrationById(registration.getId());
		if (existingReg == null) {
			System.err.println(
					"RegistrationService: Реєстрацію з ID " + registration.getId() + " не знайдено для оновлення.");
			return false;
		}

		if (studentDao.getStudentById(registration.getStudentId()) == null) {
			System.err.println("RegistrationService: Студент з ID " + registration.getStudentId()
					+ " не знайдений (для оновлення реєстрації).");
			return false;
		}
		if (subjectDao.getSubjectById(registration.getSubjectId()) == null) {
			System.err.println("RegistrationService: Предмет з ID " + registration.getSubjectId()
					+ " не знайдений (для оновлення реєстрації).");
			return false;
		}

		if (!registrationDao.updateRegistration(registration)) {
			System.err.println(
					"RegistrationService: Не вдалося оновити основний запис реєстрації ID " + registration.getId());
			return false;
		}

		if (!studentGradeService.updateGradesForRegistration(registration.getId(), commaSeparatedGrades)) {
			System.err.println(
					"RegistrationService: Не вдалося оновити оцінки для реєстрації ID " + registration.getId());
			return false;
		}
		return true;
	}

	public boolean deleteRegistration(int registrationId) {
		Registration existingReg = registrationDao.getRegistrationById(registrationId);
		if (existingReg == null) {
			System.err
					.println("RegistrationService: Реєстрацію з ID " + registrationId + " не знайдено для видалення.");
			return false;
		}
		return registrationDao.deleteRegistration(registrationId);
	}

	public List<Registration> getRegistrationsByStudentId(int studentId) {
		return registrationDao.getRegistrationsByStudentId(studentId);
	}

	public List<Registration> getRegistrationsBySubjectId(int subjectId) {
		return registrationDao.getRegistrationsBySubjectId(subjectId);
	}
}