package com.lab2school.model.dao;

import com.lab2school.model.entity.Subject;
import com.lab2school.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubjectDao {

	private static final String SQL_STATE_UNIQUE_VIOLATION = "23505";
	private static final String SQL_STATE_FOREIGN_KEY_VIOLATION = "23503";

	private Subject mapRowToSubject(ResultSet resultSet) throws SQLException {
		Subject subject = new Subject();
		subject.setId(resultSet.getInt("id"));
		subject.setName(resultSet.getString("name"));
		subject.setDescription(resultSet.getString("description"));
		return subject;
	}

	public boolean addSubject(Subject subject) {
		String sql = "INSERT INTO subjects (name, description) VALUES (?, ?)";
		boolean success = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, subject.getName());
			preparedStatement.setString(2, subject.getDescription());
			success = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			success = false;
			if (SQL_STATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
				System.err.println("DAO Помилка: Предмет з назвою '" + subject.getName() + "' вже існує. (SQLState: "
						+ e.getSQLState() + ")");
			} else {
				System.err.println("DAO Помилка SQL при додаванні предмету: " + e.getMessage() + " (SQLState: "
						+ e.getSQLState() + ")");
			}
			e.printStackTrace();
		}
		return success;
	}

	public Subject getSubjectById(int subjectId) {
		Subject subject = null;
		String sql = "SELECT id, name, description FROM subjects WHERE id = ?";
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, subjectId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					subject = mapRowToSubject(resultSet);
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні предмету за ID " + subjectId + ": " + e.getMessage());
			e.printStackTrace();
		}
		return subject;
	}

	public List<Subject> getAllSubjects() {
		List<Subject> subjects = new ArrayList<>();
		String sql = "SELECT id, name, description FROM subjects ORDER BY name";
		try (Connection connection = DatabaseUtil.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				subjects.add(mapRowToSubject(resultSet));
			}
		} catch (SQLException e) {
			System.err.println("DAO Помилка SQL при отриманні списку предметів: " + e.getMessage());
			e.printStackTrace();
		}
		return subjects;
	}

	public boolean updateSubject(Subject subject) {
		String sql = "UPDATE subjects SET name = ?, description = ? WHERE id = ?";
		boolean rowUpdated = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, subject.getName());
			preparedStatement.setString(2, subject.getDescription());
			preparedStatement.setInt(3, subject.getId());
			rowUpdated = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			rowUpdated = false;
			if (SQL_STATE_UNIQUE_VIOLATION.equals(e.getSQLState())) {
				System.err.println("DAO Помилка: Предмет з назвою '" + subject.getName()
						+ "' вже існує (при спробі оновити). (SQLState: " + e.getSQLState() + ")");
			} else {
				System.err.println("DAO Помилка SQL при оновленні предмету ID " + subject.getId() + ": "
						+ e.getMessage() + " (SQLState: " + e.getSQLState() + ")");
			}
			e.printStackTrace();
		}
		return rowUpdated;
	}

	public boolean deleteSubject(int subjectId) {
		String sql = "DELETE FROM subjects WHERE id = ?";
		boolean rowDeleted = false;
		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, subjectId);
			rowDeleted = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			rowDeleted = false;
			if (SQL_STATE_FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
				System.err.println("DAO Деталі: Неможливо видалити предмет ID " + subjectId
						+ ", оскільки на нього є посилання (наприклад, у записах студентів на цей предмет). (SQLState: "
						+ e.getSQLState() + ")");
			} else {
				System.err.println("DAO Помилка SQL при видаленні предмету ID " + subjectId + ": " + e.getMessage()
						+ " (SQLState: " + e.getSQLState() + ")");
			}
			e.printStackTrace();
		}
		return rowDeleted;
	}
}