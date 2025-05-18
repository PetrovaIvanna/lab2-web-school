package com.lab2school.model.dao;

import com.lab2school.model.entity.*;
import com.lab2school.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDao {

	private static final String SQL_STATE_UNIQUE_VIOLATION = "23505";
	private static final String SQL_STATE_FOREIGN_KEY_VIOLATION = "23503";
	private static final String BASE_SELECT_REGISTRATION_SQL = "SELECT r.id as reg_id, "
			+ "s.id as student_id, s.first_name, s.last_name, s.date_of_birth, s.school_id, "
			+ "sch.name as school_name, "
			+ "sub.id as subject_id, sub.name as subject_name, sub.description as subject_description "
			+ "FROM registrations r " + "JOIN students s ON r.student_id = s.id "
			+ "JOIN subjects sub ON r.subject_id = sub.id " + "LEFT JOIN schools sch ON s.school_id = sch.id ";

	public boolean addRegistration(Registration registration) {
		String sql = "INSERT INTO registrations (student_id, subject_id) VALUES (?, ?)";
		boolean success = false;

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setInt(1, registration.getStudentId());
			preparedStatement.setInt(2, registration.getSubjectId());

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						registration.setId(generatedKeys.getInt(1));
					}
				}
				success = true;
			}
		} catch (SQLException e) {
			success = false;
			if (SQL_STATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
				System.err.println("DAO Помилка: Студент ID " + registration.getStudentId()
						+ " вже зареєстрований на предмет ID " + registration.getSubjectId()
						+ " (Операція: додаванні реєстрації, SQLState: " + e.getSQLState() + ")");
			} else if (SQL_STATE_FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
				System.err.println("DAO Помилка: Не знайдено студента або предмета для реєстрації (ID студента: "
						+ registration.getStudentId() + ", ID предмета: " + registration.getSubjectId()
						+ ") (Операція: додаванні реєстрації, SQLState: " + e.getSQLState() + ")");
			} else {
				System.err.println("DAO Помилка SQL при додаванні реєстрації: " + e.getMessage() + " (SQLState: "
						+ e.getSQLState() + ")");
			}
			e.printStackTrace();
		}
		return success;
	}

	public boolean updateRegistration(Registration registration) {
		String sql = "UPDATE registrations SET student_id = ?, subject_id = ? WHERE id = ?";
		boolean rowUpdated = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, registration.getStudentId());
			preparedStatement.setInt(2, registration.getSubjectId());
			preparedStatement.setInt(3, registration.getId());
			rowUpdated = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			rowUpdated = false;
			if (SQL_STATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
				System.err.println("DAO Помилка: Студент ID " + registration.getStudentId()
						+ " вже зареєстрований на предмет ID " + registration.getSubjectId()
						+ " (Операція: оновленні реєстрації, SQLState: " + e.getSQLState() + ")");
			} else if (SQL_STATE_FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
				System.err.println(
						"DAO Помилка: Не знайдено студента або предмета для оновлення реєстрації (ID студента: "
								+ registration.getStudentId() + ", ID предмета: " + registration.getSubjectId()
								+ ") (Операція: оновленні реєстрації, SQLState: " + e.getSQLState() + ")");
			} else {
				System.err.println("DAO Помилка SQL при оновленні реєстрації: " + e.getMessage() + " (SQLState: "
						+ e.getSQLState() + ")");
			}
			e.printStackTrace();
		}
		return rowUpdated;
	}

	public boolean deleteRegistration(int registrationId) {
		String sql = "DELETE FROM registrations WHERE id = ?";
		boolean rowDeleted = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, registrationId);
			rowDeleted = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			rowDeleted = false;
			System.err.println("DAO Помилка SQL при видаленні реєстрації ID " + registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}
		return rowDeleted;
	}

	private Registration mapRowToRegistration(ResultSet resultSet, StudentGradeDao studentGradeDao)
			throws SQLException {
		Student student = new Student();
		student.setId(resultSet.getInt("student_id"));
		student.setFirstName(resultSet.getString("first_name"));
		student.setLastName(resultSet.getString("last_name"));
		java.sql.Date dbSqlDate = resultSet.getDate("date_of_birth");
		if (dbSqlDate != null) {
			student.setDateOfBirth(dbSqlDate.toLocalDate());
		}
		student.setSchoolId(resultSet.getInt("school_id"));
		if (resultSet.getString("school_name") != null) {
			School school = new School();
			school.setId(resultSet.getInt("school_id"));
			school.setName(resultSet.getString("school_name"));
			student.setSchool(school);
		}

		Subject subject = new Subject();
		subject.setId(resultSet.getInt("subject_id"));
		subject.setName(resultSet.getString("subject_name"));
		subject.setDescription(resultSet.getString("subject_description"));

		Registration registration = new Registration();
		registration.setId(resultSet.getInt("reg_id"));
		registration.setStudentId(student.getId());
		registration.setSubjectId(subject.getId());
		registration.setStudent(student);
		registration.setSubject(subject);

		if (studentGradeDao != null) {
			List<StudentGrade> grades = studentGradeDao.getGradesByRegistrationId(registration.getId());
			registration.setStudentGrades(grades);
		}
		return registration;
	}

	public Registration getRegistrationById(int registrationId) {
		Registration registration = null;
		String sql = BASE_SELECT_REGISTRATION_SQL + "WHERE r.id = ?";

		StudentGradeDao studentGradeDao = new StudentGradeDao();

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, registrationId);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				if (rs.next()) {
					registration = mapRowToRegistration(rs, studentGradeDao);
				}
			}
		} catch (SQLException e) {
			System.err.println("Помилка SQL при отриманні реєстрації за ID " + registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}
		return registration;
	}

	public List<Registration> getAllRegistrations() {
		List<Registration> registrationsList = new ArrayList<>();
		String sql = BASE_SELECT_REGISTRATION_SQL + "ORDER BY r.id DESC";

		StudentGradeDao studentGradeDao = new StudentGradeDao();

		try (Connection connection = DatabaseUtil.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				registrationsList.add(mapRowToRegistration(rs, studentGradeDao));
			}
		} catch (SQLException e) {
			System.err.println("Помилка SQL при отриманні списку реєстрацій: " + e.getMessage());
			e.printStackTrace();
		}
		return registrationsList;
	}

	public List<Registration> getRegistrationsByStudentId(int studentIdFilter) {
		List<Registration> registrationsList = new ArrayList<>();
		String sql = BASE_SELECT_REGISTRATION_SQL + "WHERE r.student_id = ? " + "ORDER BY sub.name";

		StudentGradeDao studentGradeDao = new StudentGradeDao();

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, studentIdFilter);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					registrationsList.add(mapRowToRegistration(rs, studentGradeDao));
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні реєстрацій за student_id " + studentIdFilter + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
		return registrationsList;
	}

	public List<Registration> getRegistrationsBySubjectId(int subjectIdFilter) {
		List<Registration> registrationsList = new ArrayList<>();
		String sql = BASE_SELECT_REGISTRATION_SQL + "WHERE r.subject_id = ? " + "ORDER BY s.last_name, s.first_name";

		StudentGradeDao studentGradeDao = new StudentGradeDao();

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, subjectIdFilter);
			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					registrationsList.add(mapRowToRegistration(rs, studentGradeDao));
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні реєстрацій за subject_id " + subjectIdFilter + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
		return registrationsList;
	}
}