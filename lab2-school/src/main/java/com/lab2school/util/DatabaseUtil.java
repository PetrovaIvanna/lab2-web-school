package com.lab2school.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtil {

	private static final Properties DB_PROPERTIES = new Properties();
	private static String JDBC_DRIVER;
	private static String DB_URL;
	private static String USER;
	private static String PASS;

	static {
		try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
			if (input == null) {
				System.err.println("Не вдалося знайти файл db.properties у classpath.");
			} else {
				DB_PROPERTIES.load(input);
				JDBC_DRIVER = DB_PROPERTIES.getProperty("driver");
				DB_URL = DB_PROPERTIES.getProperty("url");
				USER = DB_PROPERTIES.getProperty("username");
				PASS = DB_PROPERTIES.getProperty("password");

				if (JDBC_DRIVER == null || DB_URL == null || USER == null || PASS == null) {
					System.err.println(
							"Одна або декілька обов'язкових властивостей бази даних (db.driver, db.url, db.username) не знайдено або порожні у db.properties.");
					JDBC_DRIVER = null;
				}
			}
		} catch (IOException ex) {
			System.err.println("Помилка при читанні файлу db.properties: " + ex.getMessage());
			ex.printStackTrace();
			JDBC_DRIVER = null;
		}
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {
			if (JDBC_DRIVER == null || DB_URL == null || USER == null || PASS == null) {
				System.err.println("Неможливо встановити з'єднання: конфігурація БД не завантажена або неповна.");
				return null;
			}
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.err.println(
					"Помилка: JDBC Драйвер (" + (JDBC_DRIVER != null ? JDBC_DRIVER : "не вказано") + ") не знайдено.");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Помилка підключення до БД за адресою: " + DB_URL);
			System.err.println("Користувач: " + USER);
			System.err.println("Повідомлення від СУБД: " + e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}

	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("Помилка закриття Connection: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				System.err.println("Помилка закриття Statement: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void closePreparedStatement(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				System.err.println("Помилка закриття PreparedStatement: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void closeResultSet(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				System.err.println("Помилка закриття ResultSet: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}