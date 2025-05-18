package com.lab2school.model.dao;

import com.lab2school.model.entity.Student;
import com.lab2school.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

	private Student mapRowToStudent(ResultSet resultSet) throws SQLException {
		Student student = new Student();
		student.setId(resultSet.getInt("id"));
		student.setFirstName(resultSet.getString("first_name"));
		student.setLastName(resultSet.getString("last_name"));
		java.sql.Date dbSqlDate = resultSet.getDate("date_of_birth");
		if (dbSqlDate != null) {
			student.setDateOfBirth(dbSqlDate.toLocalDate());
		}
		student.setSchoolId(resultSet.getInt("school_id"));
		return student;
	}

	public boolean addStudent(Student student) {
		String sql = "INSERT INTO students (first_name, last_name, date_of_birth, school_id) VALUES (?, ?, ?, ?)";
		boolean success = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, student.getFirstName());
			preparedStatement.setString(2, student.getLastName());

			if (student.getDateOfBirth() != null) {
				preparedStatement.setObject(3, student.getDateOfBirth());
			} else {
				preparedStatement.setNull(3, Types.DATE);
			}
			preparedStatement.setInt(4, student.getSchoolId());

			success = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при додаванні студента: " + e.getMessage() + " (SQLState: "
					+ e.getSQLState() + ")");
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	public Student getStudentById(int studentId) {
		Student student = null;
		String sql = "SELECT id, first_name, last_name, date_of_birth, school_id FROM students WHERE id = ?";
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, studentId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					student = mapRowToStudent(resultSet);
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні студента за ID " + studentId + ": " + e.getMessage());
			e.printStackTrace();
		}
		return student;
	}

	public List<Student> getAllStudents() {
		List<Student> students = new ArrayList<>();
		String sql = "SELECT id, first_name, last_name, date_of_birth, school_id FROM students ORDER BY last_name, first_name";
		try (Connection connection = DatabaseUtil.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				students.add(mapRowToStudent(resultSet));
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні списку студентів: " + e.getMessage());
			e.printStackTrace();
		}
		return students;
	}

	public boolean updateStudent(Student student) {
		String sql = "UPDATE students SET first_name = ?, last_name = ?, date_of_birth = ?, school_id = ? WHERE id = ?";
		boolean rowUpdated = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, student.getFirstName());
			preparedStatement.setString(2, student.getLastName());
			if (student.getDateOfBirth() != null) {
				preparedStatement.setObject(3, student.getDateOfBirth());
			} else {
				preparedStatement.setNull(3, Types.DATE);
			}
			preparedStatement.setInt(4, student.getSchoolId());
			preparedStatement.setInt(5, student.getId());
			rowUpdated = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при оновленні студента ID " + student.getId() + ": " + e.getMessage()
					+ " (SQLState: " + e.getSQLState() + ")");
			e.printStackTrace();
			rowUpdated = false;
		}
		return rowUpdated;
	}

	public boolean deleteStudent(int studentId) {
		String sql = "DELETE FROM students WHERE id = ?";
		boolean rowDeleted = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, studentId);
			rowDeleted = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при видаленні студента ID " + studentId + ": " + e.getMessage()
					+ " (SQLState: " + e.getSQLState() + ")");
			if ("23503".equals(e.getSQLState())) {
				System.err.println("DAO Деталі: Неможливо видалити студента ID " + studentId
						+ ", оскільки він має пов'язані записи (наприклад, реєстрації на курси).");
			}
			e.printStackTrace();
			rowDeleted = false;
		}
		return rowDeleted;
	}
}