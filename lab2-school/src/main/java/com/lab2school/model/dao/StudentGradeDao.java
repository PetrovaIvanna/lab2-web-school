package com.lab2school.model.dao;

import com.lab2school.model.entity.StudentGrade;
import com.lab2school.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentGradeDao {

	private StudentGrade mapRowToStudentGrade(ResultSet resultSet) throws SQLException {
		StudentGrade grade = new StudentGrade();
		grade.setId(resultSet.getInt("id"));
		grade.setRegistrationId(resultSet.getInt("registration_id"));
		grade.setGradeValue(resultSet.getInt("grade_value"));
		return grade;
	}

	public boolean addGrade(StudentGrade studentGrade) {
		String sql = "INSERT INTO student_grades (registration_id, grade_value) VALUES (?, ?)";
		boolean success = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, studentGrade.getRegistrationId());
			preparedStatement.setInt(2, studentGrade.getGradeValue());
			success = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			success = false;
			System.err.println("DAO Помилка SQL при додаванні оцінки: " + e.getMessage());
			e.printStackTrace();
		}
		return success;
	}

	public boolean deleteGradesByRegistrationId(int registrationId) {
		String sql = "DELETE FROM student_grades WHERE registration_id = ?";
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, registrationId);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при видаленні оцінок: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public List<StudentGrade> getGradesByRegistrationId(int registrationId) {
		List<StudentGrade> grades = new ArrayList<>();
		String sql = "SELECT * FROM student_grades WHERE registration_id = ? ORDER BY id";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, registrationId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					grades.add(mapRowToStudentGrade(resultSet));
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні оцінок для registration_id " + registrationId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
		return grades;
	}
}