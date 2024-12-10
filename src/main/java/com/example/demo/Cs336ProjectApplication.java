package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.*;
import java.util.*;

@SpringBootApplication
public class Cs336ProjectApplication {
	public static class LoanFilters {
		Set<Integer> msamdList = new HashSet<>();
		Double minIncomeDebtRatio = null;
		Double maxIncomeDebtRatio = null;
		Set<String> countyList = new HashSet<>();
		Set<Integer> loanTypeList = new HashSet<>();
		Double minTractMsamdIncome = null;
		Double maxTractMsamdIncome = null;
		Set<Integer> loanPurposeList = new HashSet<>();
		Set<Integer> propertyTypeList = new HashSet<>();
		String purchaserTypeFilter = null;
		Set<Integer> ownerOccupancyList = new HashSet<>();

		public String getFilterDescription() {
			List<String> activeFilters = new ArrayList<>();

			if (!msamdList.isEmpty()) {
				activeFilters.add("MSAMD IN (" + String.join(", ", msamdList.stream().map(String::valueOf).toList()) + ")");
			}

			if (minIncomeDebtRatio != null || maxIncomeDebtRatio != null) {
				String ratioFilter = "Income/Debt Ratio: ";
				if (minIncomeDebtRatio != null && maxIncomeDebtRatio != null) {
					ratioFilter += "between " + minIncomeDebtRatio + " and " + maxIncomeDebtRatio;
				} else if (minIncomeDebtRatio != null) {
					ratioFilter += ">= " + minIncomeDebtRatio;
				} else {
					ratioFilter += "<= " + maxIncomeDebtRatio;
				}
				activeFilters.add(ratioFilter);
			}

			if (!countyList.isEmpty()) {
				activeFilters.add("County IN (" + String.join(", ", countyList) + ")");
			}

			if (!loanTypeList.isEmpty()) {
				activeFilters
						.add("Loan Type IN (" + String.join(", ", loanTypeList.stream().map(String::valueOf).toList()) + ")");
			}

			if (minTractMsamdIncome != null || maxTractMsamdIncome != null) {
				String incomeFilter = "Tract/MSAMD Income: ";
				if (minTractMsamdIncome != null && maxTractMsamdIncome != null) {
					incomeFilter += "between " + minTractMsamdIncome + " and " + maxTractMsamdIncome;
				} else if (minTractMsamdIncome != null) {
					incomeFilter += ">= " + minTractMsamdIncome;
				} else {
					incomeFilter += "<= " + maxTractMsamdIncome;
				}
				activeFilters.add(incomeFilter);
			}

			if (!loanPurposeList.isEmpty()) {
				activeFilters
						.add("Loan Purpose IN (" + String.join(", ", loanPurposeList.stream().map(String::valueOf).toList()) + ")");
			}

			if (!propertyTypeList.isEmpty()) {
				activeFilters.add(
						"Property Type IN (" + String.join(", ", propertyTypeList.stream().map(String::valueOf).toList()) + ")");
			}

			if (purchaserTypeFilter != null) {
				activeFilters.add("Purchaser Type = '" + purchaserTypeFilter + "'");
			}

			if (!ownerOccupancyList.isEmpty()) {
				activeFilters.add("Owner Occupancy IN ("
						+ String.join(", ", ownerOccupancyList.stream().map(String::valueOf).toList()) + ")");
			}

			if (activeFilters.isEmpty()) {
				return "No active filters";
			}

			return String.join(" AND ", activeFilters);
		}

		public void clearAllFilters() {
			msamdList.clear();
			minIncomeDebtRatio = null;
			maxIncomeDebtRatio = null;
			countyList.clear();
			loanTypeList.clear();
			minTractMsamdIncome = null;
			maxTractMsamdIncome = null;
			loanPurposeList.clear();
			propertyTypeList.clear();
			purchaserTypeFilter = null;
			ownerOccupancyList.clear();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Cs336ProjectApplication.class, args);

		LoanFilters filters = new LoanFilters();
		Scanner scanner = new Scanner(System.in);

		while (true) {
			displayFilterStats(filters);

			System.out.println("\nPlease choose an option:");
			System.out.println("1. Add filter");
			System.out.println("2. Delete filter");
			System.out.println("3. Calculate rate");
			System.out.println("4. Add new mortgage"); // Add this line
			System.out.println("5. Exit"); // Change this to option 5

			System.out.print("\nEnter your choice (1-5): "); // Update the range
			String choice = scanner.nextLine();

			switch (choice) {
				case "1":
					addFilter(scanner, filters);
					break;
				case "2":
					deleteFilter(scanner, filters);
					break;
				case "3":
					calculateRate(filters);
					break;
				case "4": // Add this case
					addNewMortgage(scanner);
					break;
				case "5": // Update this case number
					System.out.println("Goodbye!");
					scanner.close();
					System.exit(0);
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void displayFilterStats(LoanFilters filters) {
		System.out.println("\nCurrent Filters: " + filters.getFilterDescription());

		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) as row_count, SUM(loan_amount_000s) as total_loan_amount " +
						"FROM preliminary WHERE action_taken = 1");

		List<Object> params = new ArrayList<>();
		appendFilterConditions(sql, params, filters);

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "Test1234*");
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				System.out.println("Matching rows: " + rs.getInt("row_count"));
				System.out.println("Total loan amount: $" + rs.getLong("total_loan_amount") + "000");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void addFilter(Scanner scanner, LoanFilters filters) {
		while (true) {
			System.out.println("\nAvailable filter types (enter multiple numbers separated by commas, or 'done' to finish):");
			System.out.println("1. MSAMD");
			System.out.println("2. Income/Debt Ratio");
			System.out.println("3. County");
			System.out.println("4. Loan Type");
			System.out.println("5. Tract to MSAMD Income");
			System.out.println("6. Loan Purpose");
			System.out.println("7. Property Type");
			System.out.println("8. Purchaser Type");
			System.out.println("9. Owner Occupancy");

			System.out.print("\nSelect filter types (e.g., '1,3,4' or 'done'): ");
			String input = scanner.nextLine().trim().toLowerCase();

			if (input.equals("done")) {
				break;
			}

			String[] choices = input.split(",");
			for (String filterChoice : choices) {
				filterChoice = filterChoice.trim();
				switch (filterChoice) {
					case "1":
						System.out.print("Enter MSAMD code (or press Enter to skip): ");
						String msamdInput = scanner.nextLine().trim();
						if (!msamdInput.isEmpty()) {
							filters.msamdList.add(Integer.parseInt(msamdInput));
						}
						break;
					case "2":
						System.out.println("Income to Debt Ratio Filter");
						System.out.print("Enter minimum ratio (or press Enter to skip): ");
						String minInput = scanner.nextLine();
						if (!minInput.trim().isEmpty()) {
							try {
								double minRatio = Double.parseDouble(minInput);
								filters.minIncomeDebtRatio = minRatio;
							} catch (NumberFormatException e) {
								System.out.println("Invalid number format. Minimum ratio not set.");
							}
						}

						System.out.print("Enter maximum ratio (or press Enter to skip): ");
						String maxInput = scanner.nextLine();
						if (!maxInput.trim().isEmpty()) {
							try {
								double maxRatio = Double.parseDouble(maxInput);
								filters.maxIncomeDebtRatio = maxRatio;
							} catch (NumberFormatException e) {
								System.out.println("Invalid number format. Maximum ratio not set.");
							}
						}
						break;
					case "3":
						System.out.println("County Filter");
						System.out.println("Available counties:");
						try (Connection conn = DriverManager.getConnection(
								"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery("SELECT DISTINCT county_name FROM preliminary ORDER BY county_name");
							while (rs.next()) {
								System.out.println("- " + rs.getString("county_name"));
							}
							System.out.print("Enter county name (or press Enter to skip): ");
							String county = scanner.nextLine().trim();
							if (!county.isEmpty()) {
								filters.countyList.add(county);
							}
						} catch (SQLException e) {
							System.out.println("Error fetching counties: " + e.getMessage());
						}
						break;
					case "4":
						System.out.println("Loan Type Filter");
						System.out.println("Available loan types:");
						try (Connection conn = DriverManager.getConnection(
								"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt
									.executeQuery("SELECT DISTINCT loan_type, loan_type_name FROM preliminary ORDER BY loan_type");
							while (rs.next()) {
								System.out.println(rs.getInt("loan_type") + ": " + rs.getString("loan_type_name"));
							}
							System.out.print("Enter loan type code (or press Enter to skip): ");
							String loanType = scanner.nextLine().trim();
							if (!loanType.isEmpty()) {
								filters.loanTypeList.add(Integer.parseInt(loanType));
							}
						} catch (SQLException e) {
							System.out.println("Error fetching loan types: " + e.getMessage());
						}
						break;
					case "5":
						System.out.println("Tract to MSAMD Income Filter");
						System.out.print("Enter minimum tract/MSAMD income ratio (or press Enter to skip): ");
						String minIncome = scanner.nextLine();
						if (!minIncome.trim().isEmpty()) {
							try {
								double min = Double.parseDouble(minIncome);
								filters.minTractMsamdIncome = min;
							} catch (NumberFormatException e) {
								System.out.println("Invalid number format. Minimum income ratio not set.");
							}
						}

						System.out.print("Enter maximum tract/MSAMD income ratio (or press Enter to skip): ");
						String maxIncome = scanner.nextLine();
						if (!maxIncome.trim().isEmpty()) {
							try {
								double max = Double.parseDouble(maxIncome);
								filters.maxTractMsamdIncome = max;
							} catch (NumberFormatException e) {
								System.out.println("Invalid number format. Maximum income ratio not set.");
							}
						}
						break;
					case "6":
						System.out.println("Loan Purpose Filter");
						System.out.println("Available loan purposes:");
						try (Connection conn = DriverManager.getConnection(
								"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(
									"SELECT DISTINCT loan_purpose, loan_purpose_name FROM preliminary ORDER BY loan_purpose");
							while (rs.next()) {
								System.out.println(rs.getInt("loan_purpose") + ": " + rs.getString("loan_purpose_name"));
							}
							System.out.print("Enter loan purpose code (or press Enter to skip): ");
							String loanPurpose = scanner.nextLine().trim();
							if (!loanPurpose.isEmpty()) {
								filters.loanPurposeList.add(Integer.parseInt(loanPurpose));
							}
						} catch (SQLException e) {
							System.out.println("Error fetching loan purposes: " + e.getMessage());
						}
						break;
					case "7":
						System.out.println("Property Type Filter");
						System.out.println("Available property types:");
						try (Connection conn = DriverManager.getConnection(
								"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(
									"SELECT DISTINCT property_type, property_type_name FROM preliminary ORDER BY property_type");
							while (rs.next()) {
								System.out.println(rs.getInt("property_type") + ": " + rs.getString("property_type_name"));
							}
							System.out.print("Enter property type code (or press Enter to skip): ");
							String propertyType = scanner.nextLine().trim();
							if (!propertyType.isEmpty()) {
								filters.propertyTypeList.add(Integer.parseInt(propertyType));
							}
						} catch (SQLException e) {
							System.out.println("Error fetching property types: " + e.getMessage());
						}
						break;
					case "8":
						System.out.println("Purchaser Type Filter");
						System.out.println("Available purchaser types:");
						System.out.println("1. Fannie Mae (FNMA)");
						System.out.println("2. Ginnie Mae (GNMA)");
						System.out.println("3. Freddie Mac (FHLMC)");
						System.out.println("4. Farmer Mac (FAMC)");
						System.out.println("5. Affiliate institution");

						System.out.print("Enter choice (1-5, or press Enter to skip): ");
						String choice = scanner.nextLine().trim();
						if (!choice.isEmpty()) {
							switch (choice) {
								case "1":
									filters.purchaserTypeFilter = "Fannie Mae (FNMA)";
									break;
								case "2":
									filters.purchaserTypeFilter = "Ginnie Mae (GNMA)";
									break;
								case "3":
									filters.purchaserTypeFilter = "Freddie Mac (FHLMC)";
									break;
								case "4":
									filters.purchaserTypeFilter = "Farmer Mac (FAMC)";
									break;
								case "5":
									filters.purchaserTypeFilter = "Affiliate institution";
									break;
								default:
									System.out.println("Invalid choice. Purchaser type not set.");
							}
						}
						break;
					case "9":
						System.out.println("Owner Occupancy Filter");
						System.out.println("Available owner occupancy types:");
						try (Connection conn = DriverManager.getConnection(
								"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(
									"SELECT DISTINCT owner_occupancy, owner_occupancy_name FROM preliminary ORDER BY owner_occupancy");
							while (rs.next()) {
								System.out.println(rs.getInt("owner_occupancy") + ": " + rs.getString("owner_occupancy_name"));
							}
							System.out.print("Enter owner occupancy code (or press Enter to skip): ");
							String occupancy = scanner.nextLine().trim();
							if (!occupancy.isEmpty()) {
								filters.ownerOccupancyList.add(Integer.parseInt(occupancy));
							}
						} catch (SQLException e) {
							System.out.println("Error fetching owner occupancy types: " + e.getMessage());
						}
						break;
					default:
						System.out.println("Invalid filter type: " + filterChoice);
				}
			}
		}
	}

	private static void deleteFilter(Scanner scanner, LoanFilters filters) {
		System.out.println("\nCurrent filters:");
		System.out.println("0. Delete ALL filters");

		List<String> activeFilters = new ArrayList<>();

		if (!filters.msamdList.isEmpty()) {
			activeFilters.add("1. MSAMD filters: " + filters.msamdList);
		}
		if (filters.minIncomeDebtRatio != null || filters.maxIncomeDebtRatio != null) {
			String ratioFilter = "2. Income/Debt Ratio filters: ";
			if (filters.minIncomeDebtRatio != null) {
				ratioFilter += "min=" + filters.minIncomeDebtRatio;
			}
			if (filters.maxIncomeDebtRatio != null) {
				ratioFilter += " max=" + filters.maxIncomeDebtRatio;
			}
			activeFilters.add(ratioFilter);
		}
		if (!filters.countyList.isEmpty()) {
			activeFilters.add("3. County filters: " + filters.countyList);
		}
		if (!filters.loanTypeList.isEmpty()) {
			activeFilters.add("4. Loan Type filters: " + filters.loanTypeList);
		}
		if (filters.minTractMsamdIncome != null || filters.maxTractMsamdIncome != null) {
			String incomeFilter = "5. Tract/MSAMD Income filters: ";
			if (filters.minTractMsamdIncome != null) {
				incomeFilter += "min=" + filters.minTractMsamdIncome;
			}
			if (filters.maxTractMsamdIncome != null) {
				incomeFilter += " max=" + filters.maxTractMsamdIncome;
			}
			activeFilters.add(incomeFilter);
		}
		if (!filters.loanPurposeList.isEmpty()) {
			activeFilters.add("6. Loan Purpose filters: " + filters.loanPurposeList);
		}
		if (!filters.propertyTypeList.isEmpty()) {
			activeFilters.add("7. Property Type filters: " + filters.propertyTypeList);
		}
		if (filters.purchaserTypeFilter != null) {
			activeFilters.add("8. Purchaser Type filter: " + filters.purchaserTypeFilter);
		}
		if (!filters.ownerOccupancyList.isEmpty()) {
			activeFilters.add("9. Owner Occupancy filters: " + filters.ownerOccupancyList);
		}

		if (activeFilters.isEmpty()) {
			System.out.println("No active filters to delete.");
			return;
		}

		for (String filter : activeFilters) {
			System.out.println(filter);
		}

		System.out.print("\nSelect filter to delete (0-9): ");
		String deleteChoice = scanner.nextLine();

		switch (deleteChoice) {
			case "0":
				filters.clearAllFilters();
				System.out.println("All filters cleared.");
				break;
			case "1":
				if (!filters.msamdList.isEmpty()) {
					filters.msamdList.clear();
					System.out.println("MSAMD filters cleared.");
				}
				break;
			case "2":
				if (filters.minIncomeDebtRatio != null || filters.maxIncomeDebtRatio != null) {
					filters.minIncomeDebtRatio = null;
					filters.maxIncomeDebtRatio = null;
					System.out.println("Income/Debt Ratio filters cleared.");
				}
				break;
			case "3":
				if (!filters.countyList.isEmpty()) {
					filters.countyList.clear();
					System.out.println("County filters cleared.");
				}
				break;
			case "4":
				if (!filters.loanTypeList.isEmpty()) {
					filters.loanTypeList.clear();
					System.out.println("Loan Type filters cleared.");
				}
				break;
			case "5":
				if (filters.minTractMsamdIncome != null || filters.maxTractMsamdIncome != null) {
					filters.minTractMsamdIncome = null;
					filters.maxTractMsamdIncome = null;
					System.out.println("Tract to MSAMD Income filters cleared.");
				}
				break;
			case "6":
				if (!filters.loanPurposeList.isEmpty()) {
					filters.loanPurposeList.clear();
					System.out.println("Loan Purpose filters cleared.");
				}
				break;
			case "7":
				if (!filters.propertyTypeList.isEmpty()) {
					filters.propertyTypeList.clear();
					System.out.println("Property Type filters cleared.");
				}
				break;
			case "8":
				if (filters.purchaserTypeFilter != null) {
					filters.purchaserTypeFilter = null;
					System.out.println("Purchaser Type filter cleared.");
				}
				break;
			case "9":
				if (!filters.ownerOccupancyList.isEmpty()) {
					filters.ownerOccupancyList.clear();
					System.out.println("Owner Occupancy filters cleared.");
				}
				break;
			default:
				System.out.println("Invalid choice. No filters deleted.");
		}
	}

	private static void calculateRate(LoanFilters filters) {
		System.out.println("\nCalculating rate for current filters...");

		StringBuilder sql = new StringBuilder(
				"SELECT lien_status, rate_spread, loan_amount_000s FROM preliminary WHERE action_taken = 1");

		List<Object> params = new ArrayList<>();
		appendFilterConditions(sql, params, filters);

		double totalWeightedRate = 0;
		double totalLoanAmount = 0;
		double baseRate = 2.33;

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "password");
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int lienStatus = rs.getInt("lien_status");
				double rateSpread = rs.getDouble("rate_spread");
				double loanAmount = rs.getDouble("loan_amount_000s") * 1000;

				double effectiveRateSpread;
				if (rateSpread == 0) {
					if (lienStatus == 1) {
						effectiveRateSpread = 1.5;
					} else if (lienStatus == 2) {
						effectiveRateSpread = 3.5;
					} else {
						effectiveRateSpread = 3.5;
					}
				} else {
					effectiveRateSpread = rateSpread;
				}

				double totalRate = baseRate + effectiveRateSpread;

				totalWeightedRate += (totalRate * loanAmount);
				totalLoanAmount += loanAmount;
			}

			if (totalLoanAmount == 0) {
				System.out.println("No matching loans found.");
				return;
			}

			double finalRate = totalWeightedRate / totalLoanAmount;

			System.out.printf("\nSecuritization Summary:");
			System.out.println("\nTotal Cost of Securitization: $" + Math.round(totalLoanAmount));
			System.out.printf("\nWeighted Average Rate: %.2f%%", finalRate);

			Scanner scanner = new Scanner(System.in);
			System.out.print("\n\nDo you accept this rate and cost? (yes/no): ");
			String decision = scanner.nextLine().toLowerCase();

			if (decision.equals("yes")) {
				try {
					StringBuilder updateSql = new StringBuilder(
							"UPDATE preliminary SET purchaser_type = 1 WHERE action_taken = 1");
					appendFilterConditions(updateSql, params, filters);

					PreparedStatement updateStmt = conn.prepareStatement(updateSql.toString());
					for (int i = 0; i < params.size(); i++) {
						updateStmt.setObject(i + 1, params.get(i));
					}

					int updatedRows = updateStmt.executeUpdate();
					System.out.printf("\nSuccessfully updated %d loans to private securitization.", updatedRows);
					System.out.println("\nProgram complete. Exiting...");
					System.exit(0);

				} catch (SQLException e) {
					System.out.println("Failed to update loans: " + e.getMessage());
					System.out.println("Returning to main menu...");
				}
			} else {
				System.out.println("Rate declined. Returning to main menu...");
			}

		} catch (SQLException e) {
			System.out.println("Error calculating rate: " + e.getMessage());
		}
	}

	private static void appendFilterConditions(StringBuilder sql, List<Object> params, LoanFilters filters) {
		if (!filters.msamdList.isEmpty()) {
			sql.append(" AND msamd IN (");
			sql.append(String.join(",", Collections.nCopies(filters.msamdList.size(), "?")));
			sql.append(")");
			params.addAll(filters.msamdList);
		}

		if (filters.minIncomeDebtRatio != null) {
			sql.append(" AND (applicant_income_000s / loan_amount_000s) >= ?");
			params.add(filters.minIncomeDebtRatio);
		}
		if (filters.maxIncomeDebtRatio != null) {
			sql.append(" AND (applicant_income_000s / loan_amount_000s) <= ?");
			params.add(filters.maxIncomeDebtRatio);
		}

		if (!filters.countyList.isEmpty()) {
			sql.append(" AND county_name IN (");
			sql.append(String.join(",", Collections.nCopies(filters.countyList.size(), "?")));
			sql.append(")");
			params.addAll(filters.countyList);
		}

		if (!filters.loanTypeList.isEmpty()) {
			sql.append(" AND loan_type IN (");
			sql.append(String.join(",", Collections.nCopies(filters.loanTypeList.size(), "?")));
			sql.append(")");
			params.addAll(filters.loanTypeList);
		}

		if (!filters.loanPurposeList.isEmpty()) {
			sql.append(" AND loan_purpose IN (");
			sql.append(String.join(",", Collections.nCopies(filters.loanPurposeList.size(), "?")));
			sql.append(")");
			params.addAll(filters.loanPurposeList);
		}

		if (!filters.propertyTypeList.isEmpty()) {
			sql.append(" AND property_type IN (");
			sql.append(String.join(",", Collections.nCopies(filters.propertyTypeList.size(), "?")));
			sql.append(")");
			params.addAll(filters.propertyTypeList);
		}

		if (filters.purchaserTypeFilter != null) {
			sql.append(" AND purchaser_type_name = ?");
			params.add(filters.purchaserTypeFilter);
		}

		if (!filters.ownerOccupancyList.isEmpty()) {
			sql.append(" AND owner_occupancy IN (");
			sql.append(String.join(",", Collections.nCopies(filters.ownerOccupancyList.size(), "?")));
			sql.append(")");
			params.addAll(filters.ownerOccupancyList);
		}

		if (filters.minTractMsamdIncome != null) {
			sql.append(" AND tract_to_msamd_income >= ?");
			params.add(filters.minTractMsamdIncome);
		}
		if (filters.maxTractMsamdIncome != null) {
			sql.append(" AND tract_to_msamd_income <= ?");
			params.add(filters.maxTractMsamdIncome);
		}
	}

	private static void addNewMortgage(Scanner scanner) {
		Map<String, Object> mortgageData = new HashMap<>();
		boolean isComplete = false;

		while (!isComplete) {
			System.out.println("\nSelect information to add (or 'done' when finished):");
			System.out.println("1. Income");
			System.out.println("2. Loan Amount");
			System.out.println("3. MSAMD");
			System.out.println("4. Applicant Sex");
			System.out.println("5. Loan Type");
			System.out.println("6. Ethnicity");
			System.out.println("Type 'done' to finish");

			System.out.print("\nEnter choice: ");
			String choice = scanner.nextLine().trim();

			if (choice.equalsIgnoreCase("done")) {
				if (mortgageData.size() == 6) {
					isComplete = true;
					continue;
				} else {
					System.out.println("Please complete all required fields before proceeding.");
					continue;
				}
			}

			switch (choice) {
				case "1":
					System.out.print("Enter income (in thousands): ");
					try {
						int income = Integer.parseInt(scanner.nextLine().trim());
						mortgageData.put("applicant_income_000s", income);
					} catch (NumberFormatException e) {
						System.out.println("Invalid input. Please enter a number.");
					}
					break;

				case "2":
					System.out.print("Enter loan amount (in thousands): ");
					try {
						int loanAmount = Integer.parseInt(scanner.nextLine().trim());
						mortgageData.put("loan_amount_000s", loanAmount);
					} catch (NumberFormatException e) {
						System.out.println("Invalid input. Please enter a number.");
					}
					break;

				case "3":
					try (Connection conn = DriverManager.getConnection(
							"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(
								"SELECT DISTINCT msamd, msamd_name FROM preliminary " +
										"WHERE msamd IS NOT NULL AND msamd_name IS NOT NULL " +
										"ORDER BY msamd");
						System.out.println("\nAvailable MSAMDs:");
						while (rs.next()) {
							System.out.println(rs.getInt("msamd") + ": " + rs.getString("msamd_name"));
						}
						System.out.print("Enter MSAMD code: ");
						int msamd = Integer.parseInt(scanner.nextLine().trim());
						mortgageData.put("msamd", msamd);
					} catch (SQLException e) {
						System.out.println("Error fetching MSAMDs: " + e.getMessage());
					}
					break;

				case "4":
					System.out.println("\nApplicant Sex Options:");
					System.out.println("1: Male");
					System.out.println("2: Female");
					System.out.println("3: Information not provided");
					System.out.println("4: Not applicable");
					System.out.print("Enter choice (1-4): ");
					try {
						int sex = Integer.parseInt(scanner.nextLine().trim());
						if (sex >= 1 && sex <= 4) {
							mortgageData.put("applicant_sex", sex);
						} else {
							System.out.println("Invalid choice.");
						}
					} catch (NumberFormatException e) {
						System.out.println("Invalid input. Please enter a number.");
					}
					break;

				case "5":
					try (Connection conn = DriverManager.getConnection(
							"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(
								"SELECT DISTINCT loan_type, loan_type_name FROM preliminary " +
										"WHERE loan_type_name IS NOT NULL " +
										"GROUP BY loan_type, loan_type_name " +
										"ORDER BY loan_type");
						System.out.println("\nAvailable loan types:");
						while (rs.next()) {
							System.out.println(rs.getInt("loan_type") + ": " + rs.getString("loan_type_name"));
						}
						System.out.print("Enter loan type code: ");
						String loanTypeStr = scanner.nextLine().trim();
						if (!loanTypeStr.isEmpty()) {
							int loanType = Integer.parseInt(loanTypeStr);
							stmt = conn.createStatement();
							rs = stmt.executeQuery(
									"SELECT loan_type FROM preliminary " +
											"WHERE loan_type = " + loanType + " AND loan_type_name IS NOT NULL " +
											"LIMIT 1");
							if (rs.next()) {
								mortgageData.put("loan_type", loanType);
							} else {
								System.out.println("Invalid loan type code.");
							}
						}
					} catch (SQLException e) {
						System.out.println("Error fetching loan types: " + e.getMessage());
					}
					break;

				case "6":
					System.out.println("\nEthnicity Options:");
					System.out.println("1: Hispanic or Latino");
					System.out.println("2: Not Hispanic or Latino");
					System.out.println("3: Information not provided");
					System.out.println("4: Not applicable");
					System.out.print("Enter choice (1-4): ");
					try {
						int ethnicity = Integer.parseInt(scanner.nextLine().trim());
						if (ethnicity >= 1 && ethnicity <= 4) {
							mortgageData.put("applicant_ethnicity", ethnicity);
						} else {
							System.out.println("Invalid choice.");
						}
					} catch (NumberFormatException e) {
						System.out.println("Invalid input. Please enter a number.");
					}
					break;

				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {

			PreparedStatement locationStmt = conn.prepareStatement(
					"SELECT state_code, county_code, census_tract_number FROM preliminary " +
							"WHERE msamd = ? AND state_code IS NOT NULL AND county_code IS NOT NULL " +
							"LIMIT 1");
			locationStmt.setInt(1, (Integer) mortgageData.get("msamd"));
			ResultSet locationRs = locationStmt.executeQuery();

			if (locationRs.next()) {
				mortgageData.put("state_code", locationRs.getInt("state_code"));
				mortgageData.put("county_code", locationRs.getInt("county_code"));
				mortgageData.put("census_tract_number", locationRs.getInt("census_tract_number"));
			}

			mortgageData.put("action_taken", 1);

			StringBuilder sql = new StringBuilder("INSERT INTO preliminary (");
			StringBuilder values = new StringBuilder("VALUES (");
			List<Object> params = new ArrayList<>();

			for (Map.Entry<String, Object> entry : mortgageData.entrySet()) {
				sql.append(entry.getKey()).append(", ");
				values.append("?, ");
				params.add(entry.getValue());
			}

			sql.setLength(sql.length() - 2);
			values.setLength(values.length() - 2);

			sql.append(") ").append(values).append(")");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println("New mortgage successfully added to the database!");
			} else {
				System.out.println("Failed to add new mortgage.");
			}

		} catch (SQLException e) {
			System.out.println("Error adding new mortgage: " + e.getMessage());
		}
	}
}