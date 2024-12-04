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

	// 				try (Connection connection = DriverManager.getConnection(url, user, password);
	// 						Statement stmt = connection.createStatement();
	// 						ResultSet rs = stmt.executeQuery(query)) {
	
	// 				// Process the result set
	// 				while (rs.next()) {
	// 						int loan_amount= rs.getInt("loan_amount");
	// 						int as_of_year= rs.getInt("as_of_year");
	// 						String respondent_id= rs.getString("respondent_id");
	// 						String agency_name= rs.getString("agency_name");
	// 						String agency_abbr= rs.getString("agency_abbr");
	// 						int agency_code= rs.getInt("agency_code");
	// 						String loan_type_name= rs.getString("loan_type_name");
	// 						int loan_type= rs.getInt("loan_type");
	// 						String property_type_name= rs.getString("property_type_name");
	// 						int property_type= rs.getInt("property_type");
	// 						String loan_purpose_name= rs.getString("loan_purpose_name");
	// 						int loan_purpose= rs.getInt("loan_purpose");
	// 						String owner_occupancy_name= rs.getString("owner_occupancy_name");
	// 						int owner_occupancy= rs.getInt("owner_occupancy");
	// 						String preapproval_name= rs.getString("preapproval_name");
	// 						int preapproval= rs.getInt("preapproval");
	// 						String action_take_name= rs.getString("action_take_name");
	// 						int action_take= rs.getInt("action_take");
	// 						String msamd_name= rs.getString("msamd_name");
	// 						int msamd= rs.getInt("msamd");
	// 						int state_id = rs.getInt("state_id");
	// 						String state_name = rs.getString("state_name");
	// 						String state_abbr = rs.getString("state_abbr");
	// 						int state_code = rs.getInt("state_code");
	// 						String county_name = rs.getString("county_name");
	// 						int county_code = rs.getInt("county_code");
	// 						int census_tract_number = rs.getInt("census_tract_number");
	// 						String ethnicity_name = rs.getString("ethnicity_name");
	// 						int ethnicity = rs.getInt("ethnicity");
	// 						String co_applicant_ethnicity_name = rs.getString("co_applicant_ethnicity_name");
	// 						int co_applicant_ethnicity = rs.getInt("co_applicant_ethnicity");
	// 						String race = rs.getString("race");
	// 						int race_number = rs.getInt("race_number");
	// 						String co_applicant_race_name = rs.getString("co_applicant_race_name");
	// 						int co_applicant_race_code = rs.getInt("co_applicant_race_code");
	// 						String applicant_sex_name = rs.getString("applicant_sex_name");
	// 						int applicant_sex = rs.getInt("applicant_sex");
	// 						String co_applicant_sex_name = rs.getString("co_applicant_sex_name");
	// 						int co_applicant_sex = rs.getInt("co_applicant_sex");
	// 						int applicant_income_000s = rs.getInt("applicant_income_000s");
	// 						String purchaser_type_name = rs.getString("purchaser_type_name");
	// 						int purchaser_type = rs.getInt("purchaser_type");
	// 						String denial_reason_name = rs.getString("denial_reason_name");
	// 						int denial_reason = rs.getInt("denial_reason");
	// 						int rspread = rs.getInt("rspread");
	// 						String hoepa_status_name = rs.getString("hoepa_status_name");
	// 						int hoepa_status = rs.getInt("hoepa_status");
	// 						String lien_status_name = rs.getString("lien_status_name");
	// 						int lien_status = rs.getInt("lien_status");
	// 						int population = rs.getInt("population");
	// 						int minority_population = rs.getInt("minority_population");
	// 						int hud_median_family_income = rs.getInt("hud_median_family_income");
	// 						int tract_to_msamd_income = rs.getInt("tract_to_msamd_income");
	// 						int number_of_owner_occupied_units = rs.getInt("number_of_owner_occupied_units");
	// 						int number_of_1_to_4_family_units = rs.getInt("number_of_1_to_4_family_units");
	// 						String edit_status_name = rs.getString("edit_status_name");
	// 						String edit_status = rs.getString("edit_status");
	// 						String sequence = rs.getString("sequence");
	// 						String application_date_indicator = rs.getString("application_date_indicator");
	
	// 						// Print all the values
	// 						System.out.println("Loan Amount: " + loan_amount);
	// 						System.out.println("As of Year: " + as_of_year);
	// 						System.out.println("Respondent ID: " + respondent_id);
	// 						System.out.println("Agency Name: " + agency_name);
	// 						System.out.println("Agency Abbreviation: " + agency_abbr);
	// 						System.out.println("Agency Code: " + agency_code);
	// 						System.out.println("Loan Type Name: " + loan_type_name);
	// 						System.out.println("Loan Type: " + loan_type);
	// 						System.out.println("Property Type Name: " + property_type_name);
	// 						System.out.println("Property Type: " + property_type);
	// 						System.out.println("Loan Purpose Name: " + loan_purpose_name);
	// 						System.out.println("Loan Purpose: " + loan_purpose);
	// 						System.out.println("Owner Occupancy Name: " + owner_occupancy_name);
	// 						System.out.println("Owner Occupancy: " + owner_occupancy);
	// 						System.out.println("Preapproval Name: " + preapproval_name);
	// 						System.out.println("Preapproval: " + preapproval);
	// 						System.out.println("Action Take Name: " + action_take_name);
	// 						System.out.println("Action Take: " + action_take);
	// 						System.out.println("MSAMD Name: " + msamd_name);
	// 						System.out.println("MSAMD: " + msamd);
	// 						System.out.println("State ID: " + state_id);
	// 						System.out.println("State Name: " + state_name);
	// 						System.out.println("State Abbreviation: " + state_abbr);
	// 						System.out.println("State Code: " + state_code);
	// 						System.out.println("County Name: " + county_name);
	// 						System.out.println("County Code: " + county_code);
	// 						System.out.println("Census Tract Number: " + census_tract_number);
	// 						System.out.println("Ethnicity Name: " + ethnicity_name);
	// 						System.out.println("Ethnicity: " + ethnicity);
	// 						System.out.println("Co-Applicant Ethnicity Name: " + co_applicant_ethnicity_name);
	// 						System.out.println("Co-Applicant Ethnicity: " + co_applicant_ethnicity);
	// 						System.out.println("Race: " + race);
	// 						System.out.println("Race Number: " + race_number);
	// 						System.out.println("Co-Applicant Race Name: " + co_applicant_race_name);
	// 						System.out.println("Co-Applicant Race Code: " + co_applicant_race_code);
	// 						System.out.println("Applicant Sex Name: " + applicant_sex_name);
	// 						System.out.println("Applicant Sex: " + applicant_sex);
	// 						System.out.println("Co-Applicant Sex Name: " + co_applicant_sex_name);
	// 						System.out.println("Co-Applicant Sex: " + co_applicant_sex);
	// 						System.out.println("Applicant Income (000s): " + applicant_income_000s);
	// 						System.out.println("Purchaser Type Name: " + purchaser_type_name);
	// 						System.out.println("Purchaser Type: " + purchaser_type);
	// 						System.out.println("Denial Reason Name: " + denial_reason_name);
	// 						System.out.println("Denial Reason: " + denial_reason);
	// 						System.out.println("Rspread: " + rspread);
	// 						System.out.println("HOEPA Status Name: " + hoepa_status_name);
	// 						System.out.println("HOEPA Status: " + hoepa_status);
	// 						System.out.println("Lien Status Name: " + lien_status_name);
	// 						System.out.println("Lien Status: " + lien_status);
	// 						System.out.println("Population: " + population);
	// 						System.out.println("Minority Population: " + minority_population);
	// 						System.out.println("HUD Median Family Income: " + hud_median_family_income);
	// 						System.out.println("Tract to MSAMD Income: " + tract_to_msamd_income);
	// 						System.out.println("Number of Owner-Occupied Units: " + number_of_owner_occupied_units);
	// 						System.out.println("Number of 1 to 4 Family Units: " + number_of_1_to_4_family_units);
	// 						System.out.println("Edit Status Name: " + edit_status_name);
	// 						System.out.println("Edit Status: " + edit_status);
	// 						System.out.println("Sequence: " + sequence);
	// 						System.out.println("Application Date Indicator: " + application_date_indicator);
	// 						System.out.println("------------------------------------");
	// 				}
	
	// 				} catch (SQLException e) {
	// 						// Print error message if connection or query fails
	// 						System.out.println("Error: " + e.getMessage());
	// 						e.printStackTrace();
	// 				}
	// 		}
	// }