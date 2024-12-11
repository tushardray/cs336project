package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.sql.*;
import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class Cs336ProjectApplication {

	public static class LoanFilters {
		private Set<Integer> msamdList = new HashSet<>();
		private Double minIncomeDebtRatio;
		private Double maxIncomeDebtRatio;
		private Set<String> countyList = new HashSet<>();
		private Set<Integer> loanTypeList = new HashSet<>();
		private Double minTractMsamdIncome;
		private Double maxTractMsamdIncome;
		private Set<Integer> loanPurposeList = new HashSet<>();
		private Set<Integer> propertyTypeList = new HashSet<>();
		private String purchaserTypeFilter;
		private Set<Integer> ownerOccupancyList = new HashSet<>();

		// Default constructor
		public LoanFilters() {
		}

		// Getters and setters
		public Set<Integer> getMsamdList() {
			return msamdList;
		}

		public void setMsamdList(Set<Integer> msamdList) {
			this.msamdList = msamdList;
		}

		public Double getMinIncomeDebtRatio() {
			return minIncomeDebtRatio;
		}

		public void setMinIncomeDebtRatio(Double minIncomeDebtRatio) {
			this.minIncomeDebtRatio = minIncomeDebtRatio;
		}

		public Double getMaxIncomeDebtRatio() {
			return maxIncomeDebtRatio;
		}

		public void setMaxIncomeDebtRatio(Double maxIncomeDebtRatio) {
			this.maxIncomeDebtRatio = maxIncomeDebtRatio;
		}

		public Set<String> getCountyList() {
			return countyList;
		}

		public void setCountyList(Set<String> countyList) {
			this.countyList = countyList;
		}

		public Set<Integer> getLoanTypeList() {
			return loanTypeList;
		}

		public void setLoanTypeList(Set<Integer> loanTypeList) {
			this.loanTypeList = loanTypeList;
		}

		public Double getMinTractMsamdIncome() {
			return minTractMsamdIncome;
		}

		public void setMinTractMsamdIncome(Double minTractMsamdIncome) {
			this.minTractMsamdIncome = minTractMsamdIncome;
		}

		public Double getMaxTractMsamdIncome() {
			return maxTractMsamdIncome;
		}

		public void setMaxTractMsamdIncome(Double maxTractMsamdIncome) {
			this.maxTractMsamdIncome = maxTractMsamdIncome;
		}

		public Set<Integer> getLoanPurposeList() {
			return loanPurposeList;
		}

		public void setLoanPurposeList(Set<Integer> loanPurposeList) {
			this.loanPurposeList = loanPurposeList;
		}

		public Set<Integer> getPropertyTypeList() {
			return propertyTypeList;
		}

		public void setPropertyTypeList(Set<Integer> propertyTypeList) {
			this.propertyTypeList = propertyTypeList;
		}

		public String getPurchaserTypeFilter() {
			return purchaserTypeFilter;
		}

		public void setPurchaserTypeFilter(String purchaserTypeFilter) {
			this.purchaserTypeFilter = purchaserTypeFilter;
		}

		public Set<Integer> getOwnerOccupancyList() {
			return ownerOccupancyList;
		}

		public void setOwnerOccupancyList(Set<Integer> ownerOccupancyList) {
			this.ownerOccupancyList = ownerOccupancyList;
		}

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

		public void deleteFilter(String filterName) {
			switch (filterName.toLowerCase()) {
				case "msamd":
					msamdList.clear();
					break;
				case "incomedebt":
					minIncomeDebtRatio = null;
					maxIncomeDebtRatio = null;
					break;
				case "county":
					countyList.clear();
					break;
				case "loantype":
					loanTypeList.clear();
					break;
				case "tractmsamdincome":
					minTractMsamdIncome = null;
					maxTractMsamdIncome = null;
					break;
				case "loanpurpose":
					loanPurposeList.clear();
					break;
				case "propertytype":
					propertyTypeList.clear();
					break;
				case "purchasertype":
					purchaserTypeFilter = null;
					break;
				case "owneroccupancy":
					ownerOccupancyList.clear();
					break;
				default:
					throw new IllegalArgumentException("Unknown filter name: " + filterName);
			}
		}

		public void deleteFilters(List<String> filterNames) {
			for (String filterName : filterNames) {
				deleteFilter(filterName);
			}
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(Cs336ProjectApplication.class, args);
	}

	private static void appendFilterConditions(StringBuilder sql, List<Object> params, LoanFilters filters) {
		if (!filters.getMsamdList().isEmpty()) {
			sql.append(" AND msamd IN (");
			sql.append(String.join(",", Collections.nCopies(filters.getMsamdList().size(), "?")));
			sql.append(")");
			params.addAll(filters.getMsamdList());
		}

		if (filters.getMinIncomeDebtRatio() != null) {
			sql.append(" AND (applicant_income_000s / loan_amount_000s) >= ?");
			params.add(filters.getMinIncomeDebtRatio());
		}
		if (filters.getMaxIncomeDebtRatio() != null) {
			sql.append(" AND (applicant_income_000s / loan_amount_000s) <= ?");
			params.add(filters.getMaxIncomeDebtRatio());
		}

		if (!filters.getCountyList().isEmpty()) {
			sql.append(" AND county_name IN (");
			sql.append(String.join(",", Collections.nCopies(filters.getCountyList().size(), "?")));
			sql.append(")");
			params.addAll(filters.getCountyList());
		}

		if (!filters.getLoanTypeList().isEmpty()) {
			sql.append(" AND loan_type IN (");
			sql.append(String.join(",", Collections.nCopies(filters.getLoanTypeList().size(), "?")));
			sql.append(")");
			params.addAll(filters.getLoanTypeList());
		}

		if (!filters.getLoanPurposeList().isEmpty()) {
			sql.append(" AND loan_purpose IN (");
			sql.append(String.join(",", Collections.nCopies(filters.getLoanPurposeList().size(), "?")));
			sql.append(")");
			params.addAll(filters.getLoanPurposeList());
		}

		if (!filters.getPropertyTypeList().isEmpty()) {
			sql.append(" AND property_type IN (");
			sql.append(String.join(",", Collections.nCopies(filters.getPropertyTypeList().size(), "?")));
			sql.append(")");
			params.addAll(filters.getPropertyTypeList());
		}

		if (filters.getPurchaserTypeFilter() != null) {
			sql.append(" AND purchaser_type_name = ?");
			params.add(filters.getPurchaserTypeFilter());
		}

		if (!filters.getOwnerOccupancyList().isEmpty()) {
			sql.append(" AND owner_occupancy IN (");
			sql.append(String.join(",", Collections.nCopies(filters.getOwnerOccupancyList().size(), "?")));
			sql.append(")");
			params.addAll(filters.getOwnerOccupancyList());
		}

		if (filters.getMinTractMsamdIncome() != null) {
			sql.append(" AND tract_to_msamd_income >= ?");
			params.add(filters.getMinTractMsamdIncome());
		}
		if (filters.getMaxTractMsamdIncome() != null) {
			sql.append(" AND tract_to_msamd_income <= ?");
			params.add(filters.getMaxTractMsamdIncome());
		}
	}

	@PostMapping("/stats")
	public ResponseEntity<Map<String, Object>> getFilterStats(@RequestBody LoanFilters filters) {
		Map<String, Object> response = new HashMap<>();

		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) as row_count, SUM(loan_amount_000s) as total_loan_amount " +
						"FROM preliminary WHERE action_taken = 1");

		List<Object> params = new ArrayList<>();
		appendFilterConditions(sql, params, filters);

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "password");
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				response.put("matchingRows", rs.getInt("row_count"));
				response.put("totalLoanAmount", rs.getLong("total_loan_amount") * 1000);
				response.put("activeFilters", filters.getFilterDescription());
			}

			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/filter-options")
	public ResponseEntity<Map<String, Object>> getFilterOptions() {
		Map<String, Object> options = new HashMap<>();

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {

			// Fetch counties
			List<Map<String, Object>> counties = new ArrayList<>();
			ResultSet rs = conn.createStatement().executeQuery(
					"SELECT DISTINCT county_name FROM preliminary WHERE county_name IS NOT NULL ORDER BY county_name");
			while (rs.next()) {
				String countyName = rs.getString("county_name");
				if (countyName != null) {
					HashMap<String, Object> county = new HashMap<>();
					county.put("name", countyName);
					counties.add(county);
				}
			}
			options.put("counties", counties);

			// Fetch loan types
			List<Map<String, Object>> loanTypes = new ArrayList<>();
			rs = conn.createStatement().executeQuery(
					"SELECT DISTINCT loan_type, loan_type_name FROM preliminary WHERE loan_type IS NOT NULL ORDER BY loan_type");
			while (rs.next()) {
				HashMap<String, Object> loanType = new HashMap<>();
				loanType.put("id", rs.getInt("loan_type"));
				loanType.put("name",
						rs.getString("loan_type_name") != null ? rs.getString("loan_type_name") : "Type " + rs.getInt("loan_type"));
				loanTypes.add(loanType);
			}
			options.put("loanTypes", loanTypes);

			// Fetch loan purposes
			List<Map<String, Object>> loanPurposes = new ArrayList<>();
			rs = conn.createStatement().executeQuery(
					"SELECT DISTINCT loan_purpose, loan_purpose_name FROM preliminary WHERE loan_purpose IS NOT NULL ORDER BY loan_purpose");
			while (rs.next()) {
				HashMap<String, Object> purpose = new HashMap<>();
				purpose.put("id", rs.getInt("loan_purpose"));
				purpose.put("name", rs.getString("loan_purpose_name") != null ? rs.getString("loan_purpose_name")
						: "Purpose " + rs.getInt("loan_purpose"));
				loanPurposes.add(purpose);
			}
			options.put("loanPurposes", loanPurposes);

			// Fetch property types
			List<Map<String, Object>> propertyTypes = new ArrayList<>();
			rs = conn.createStatement().executeQuery(
					"SELECT DISTINCT property_type, property_type_name FROM preliminary WHERE property_type IS NOT NULL ORDER BY property_type");
			while (rs.next()) {
				HashMap<String, Object> propertyType = new HashMap<>();
				propertyType.put("id", rs.getInt("property_type"));
				propertyType.put("name", rs.getString("property_type_name") != null ? rs.getString("property_type_name")
						: "Property " + rs.getInt("property_type"));
				propertyTypes.add(propertyType);
			}
			options.put("propertyTypes", propertyTypes);

			// Fetch owner occupancy types
			List<Map<String, Object>> ownerOccupancyTypes = new ArrayList<>();
			rs = conn.createStatement().executeQuery(
					"SELECT DISTINCT owner_occupancy, owner_occupancy_name FROM preliminary WHERE owner_occupancy IS NOT NULL ORDER BY owner_occupancy");
			while (rs.next()) {
				HashMap<String, Object> occupancyType = new HashMap<>();
				occupancyType.put("id", rs.getInt("owner_occupancy"));
				occupancyType.put("name", rs.getString("owner_occupancy_name") != null ? rs.getString("owner_occupancy_name")
						: "Occupancy " + rs.getInt("owner_occupancy"));
				ownerOccupancyTypes.add(occupancyType);
			}
			options.put("ownerOccupancyTypes", ownerOccupancyTypes);

			return ResponseEntity.ok(options);

		} catch (SQLException e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.internalServerError().body(errorResponse);
		}
	}

	@PostMapping("/calculate-rate")
	public ResponseEntity<Map<String, Object>> calculateRate(@RequestBody LoanFilters filters) {
		Map<String, Object> response = new HashMap<>();

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

				double effectiveRateSpread = rateSpread == 0
						? (lienStatus == 1 ? 1.5 : 3.5)
						: rateSpread;

				double totalRate = baseRate + effectiveRateSpread;
				totalWeightedRate += (totalRate * loanAmount);
				totalLoanAmount += loanAmount;
			}

			if (totalLoanAmount == 0) {
				return ResponseEntity.ok(Map.of("message", "No matching loans found"));
			}

			double finalRate = totalWeightedRate / totalLoanAmount;

			response.put("totalCost", Math.round(totalLoanAmount));
			response.put("weightedAverageRate", Math.round(finalRate * 100.0) / 100.0);

			return ResponseEntity.ok(response);

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/accept-rate")
	public ResponseEntity<Map<String, Object>> acceptRate(@RequestBody LoanFilters filters) {
		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {

			StringBuilder sql = new StringBuilder(
					"UPDATE preliminary SET purchaser_type = 1 WHERE action_taken = 1");

			List<Object> params = new ArrayList<>();
			appendFilterConditions(sql, params, filters);

			PreparedStatement stmt = conn.prepareStatement(sql.toString());
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			int updatedRows = stmt.executeUpdate();
			return ResponseEntity.ok(Map.of(
					"message", "Successfully updated loans",
					"updatedLoans", updatedRows));

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/mortgage")
	public ResponseEntity<Map<String, Object>> addNewMortgage(@RequestBody Map<String, Object> mortgageData) {
		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5433/postgres", "postgres", "password")) {

			// Fetch location data based on MSAMD and county
			PreparedStatement locationStmt = conn.prepareStatement(
					"SELECT state_code, county_code, census_tract_number FROM preliminary " +
							"WHERE msamd = ? AND LOWER(county) = LOWER(?) " +
							"AND state_code IS NOT NULL AND county_code IS NOT NULL " +
							"LIMIT 1");
			locationStmt.setInt(1, ((Number) mortgageData.get("msamd")).intValue());
			locationStmt.setString(2, (String) mortgageData.get("county"));
			ResultSet locationRs = locationStmt.executeQuery();

			if (!locationRs.next()) {
				// If no exact match, try to find any entry with the same MSAMD as fallback
				locationStmt = conn.prepareStatement(
						"SELECT state_code, county_code, census_tract_number FROM preliminary " +
								"WHERE msamd = ? AND state_code IS NOT NULL AND county_code IS NOT NULL " +
								"LIMIT 1");
				locationStmt.setInt(1, ((Number) mortgageData.get("msamd")).intValue());
				locationRs = locationStmt.executeQuery();

				if (!locationRs.next()) {
					return ResponseEntity.badRequest()
							.body(Map.of("error", "Could not find location data for the given MSAMD and county"));
				}
			}

			// Add location data to mortgage data
			mortgageData.put("state_code", locationRs.getInt("state_code"));
			mortgageData.put("county_code", locationRs.getInt("county_code"));
			mortgageData.put("census_tract_number", locationRs.getInt("census_tract_number"));
			mortgageData.put("action_taken", 1);

			// Build and execute insert query
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
				return ResponseEntity.ok(Map.of("message", "New mortgage successfully added"));
			} else {
				return ResponseEntity.badRequest().body(Map.of("error", "Failed to add mortgage"));
			}

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/filters/all")
	public ResponseEntity<Map<String, Object>> clearAllFilters(@RequestBody LoanFilters filters) {
		filters.clearAllFilters();
		return ResponseEntity.ok(Map.of(
				"message", "All filters cleared",
				"remainingFilters", filters.getFilterDescription()));
	}

	public static class DeleteFiltersRequest {
		private LoanFilters filters;
		private List<String> filterNames;

		public LoanFilters getFilters() {
			return filters;
		}

		public void setFilters(LoanFilters filters) {
			this.filters = filters;
		}

		public List<String> getFilterNames() {
			return filterNames;
		}

		public void setFilterNames(List<String> filterNames) {
			this.filterNames = filterNames;
		}
	}

	@DeleteMapping("/filters")
	public ResponseEntity<Map<String, Object>> deleteFilters(@RequestBody DeleteFiltersRequest request) {
		try {
			if (request.getFilters() == null || request.getFilterNames() == null) {
				return ResponseEntity.badRequest().body(Map.of(
						"error", "Both filters and filterNames are required"));
			}

			request.getFilters().deleteFilters(request.getFilterNames());
			return ResponseEntity.ok(Map.of(
					"message", "Selected filters deleted",
					"deletedFilters", request.getFilterNames(),
					"remainingFilters", request.getFilters().getFilterDescription()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of(
					"error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of(
					"error", e.getMessage()));
		}
	}
}