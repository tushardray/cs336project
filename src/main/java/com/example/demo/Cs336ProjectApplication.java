package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

@SpringBootApplication
public class Cs336ProjectApplication {

	public static void main(String[] args) {
//		mvn spring-boot:run

		SpringApplication.run(Cs336ProjectApplication.class, args);
		System.out.println("hello world");

		String SQL_SELECT = "Select * FROM joined WHERE action_take=1";

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/postgres", "postgres", "Test1234*");
			 PreparedStatement statement = conn.prepareStatement(SQL_SELECT)
		) {
			ResultSet results = statement.executeQuery();
			
			int total = 0;
			while (results.next()) {
				total = results.getInt("count");
			}
			System.out.println(total);
		}

		catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		}

	}

