package com.lab2school.model.dao;

import com.lab2school.model.entity.School;
import com.lab2school.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SchoolDao {

	private School mapRowToSchool(ResultSet resultSet) throws SQLException {
		School school = new School();
		school.setId(resultSet.getInt("id"));
		school.setName(resultSet.getString("name"));
		school.setAddress(resultSet.getString("address"));
		return school;
	}

	public List<School> getAllSchools() {
		List<School> schools = new ArrayList<>();
		String sql = "SELECT * FROM schools ORDER BY name";

		try (Connection connection = DatabaseUtil.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				schools.add(mapRowToSchool(resultSet));
			}
		} catch (SQLException e) {
			System.err.println("Помилка SQL при отриманні списку шкіл: " + e.getMessage());
			e.printStackTrace();
		}
		return schools;
	}

	public School getSchoolById(int schoolId) {
		School school = null;
		String sql = "SELECT * FROM schools WHERE id = ?";

		try (Connection connection = DatabaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, schoolId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					school = mapRowToSchool(resultSet);
				}
			}
		} catch (SQLException e) {
			System.err.println("Помилка SQL при отриманні школи за ID " + schoolId + ": " + e.getMessage());
			e.printStackTrace();
		}
		return school;
	}
}