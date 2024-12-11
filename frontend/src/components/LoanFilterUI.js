import React, { useState, useEffect } from "react";

export default function LoanFilterUI() {
  const [filterOptions, setFilterOptions] = useState({
    counties: [],
    loanTypes: [],
    loanPurposes: [],
    propertyTypes: [],
    ownerOccupancyTypes: [],
  });

  const [selectedFilters, setSelectedFilters] = useState({
    msamdList: [],
    minIncomeDebtRatio: null,
    maxIncomeDebtRatio: null,
    countyList: [],
    loanTypeList: [],
    minTractMsamdIncome: null,
    maxTractMsamdIncome: null,
    loanPurposeList: [],
    propertyTypeList: [],
    purchaserTypeFilter: null,
    ownerOccupancyList: [],
  });

  const [stats, setStats] = useState(null);
  const [rate, setRate] = useState(null);
  const [error, setError] = useState(null);

  // Fetch initial filter options
  useEffect(() => {
    fetchFilterOptions();
  }, []);

  const fetchFilterOptions = () => {
    fetch("http://localhost:8080/api/filter-options")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch filter options");
        return res.json();
      })
      .then((data) => setFilterOptions(data))
      .catch((err) => {
        console.error("Error fetching options:", err);
        setError(err.message);
      });
  };

  const updateStats = () => {
    fetchStats(selectedFilters);
  };

  const fetchStats = (filters) => {
    setError(null);

    try {
      // Ensure only plain JSON-serializable values are included
      const filterData = JSON.parse(JSON.stringify(filters));

      // Determine if there are no active filters
      const isEmptyFilter = Object.values(filterData).every(
        (value) =>
          value === null || (Array.isArray(value) && value.length === 0)
      );

      fetch("http://localhost:8080/api/stats", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(isEmptyFilter ? {} : filterData),
      })
        .then((res) => {
          if (!res.ok) throw new Error("Failed to fetch stats");
          return res.json();
        })
        .then((data) => setStats(data))
        .catch((err) => {
          console.error("Error fetching stats:", err);
          setError(err.message);
        });
    } catch (error) {
      console.error("Error serializing filters:", error);
      setError("Failed to process filters. Please check the input.");
    }
  };

  const calculateRate = () => {
    fetchRate(selectedFilters);
  };

  const fetchRate = (filters) => {
    setError(null);
    const filterData = { ...filters };
    Object.keys(filterData).forEach((key) => {
      if (
        filterData[key] === null ||
        (Array.isArray(filterData[key]) && filterData[key].length === 0)
      ) {
        delete filterData[key];
      }
    });

    // Convert MSAMD to an array if it's a single value
    if (typeof filterData.msamdList === "number") {
      filterData.msamdList = [filterData.msamdList];
    }

    fetch("http://localhost:8080/api/calculate-rate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(filterData),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to calculate rate");
        return res.json();
      })
      .then((data) => setRate(data))
      .catch((err) => {
        console.error("Error calculating rate:", err);
        setError(err.message);
      });
  };

  const acceptRate = () => {
    setError(null);
    const filterData = { ...selectedFilters };
    Object.keys(filterData).forEach((key) => {
      if (
        filterData[key] === null ||
        (Array.isArray(filterData[key]) && filterData[key].length === 0)
      ) {
        delete filterData[key];
      }
    });

    fetch("http://localhost:8080/api/accept-rate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(filterData),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to accept rate");
        return res.json();
      })
      .then((data) => {
        alert(`Successfully updated ${data.updatedLoans} loans`);
        setRate(null);
      })
      .catch((err) => {
        console.error("Error accepting rate:", err);
        setError(err.message);
      });
  };

  const handleNumberInput = (field, value) => {
    const numValue = value === "" ? null : parseFloat(value);
    setSelectedFilters((prev) => ({ ...prev, [field]: numValue }));
  };

  return (
    <div>
      <h1>Loan Filter and Rate Calculator</h1>

      {error && (
        <div style={{ color: "red", marginBottom: "20px" }}>Error: {error}</div>
      )}

      {/* 1. MSAMD Filter */}
      <div>
        <h2>MSAMD</h2>
        <input
          type="number"
          placeholder="Enter MSAMD code"
          onChange={(e) => {
            const value = e.target.value;
            if (value === "") {
              setSelectedFilters((prev) => ({ ...prev, msamdList: [] }));
            } else {
              setSelectedFilters((prev) => ({
                ...prev,
                msamdList: [parseInt(value)],
              }));
            }
          }}
        />
      </div>

      {/* 2. Income/Debt Ratio Filter */}
      <div>
        <h2>Income/Debt Ratio</h2>
        <input
          type="number"
          placeholder="Min ratio"
          step="0.1"
          onChange={(e) =>
            handleNumberInput("minIncomeDebtRatio", e.target.value)
          }
        />
        <input
          type="number"
          placeholder="Max ratio"
          step="0.1"
          onChange={(e) =>
            handleNumberInput("maxIncomeDebtRatio", e.target.value)
          }
        />
      </div>

      {/* 3. County Filter */}
      <div>
        <h2>Counties</h2>
        <select
          multiple
          value={selectedFilters.countyList}
          onChange={(e) => {
            const selected = Array.from(
              e.target.selectedOptions,
              (option) => option.value
            );
            setSelectedFilters((prev) => ({ ...prev, countyList: selected }));
          }}
        >
          {filterOptions.counties?.map((county) => (
            <option key={county.name} value={county.name}>
              {county.name}
            </option>
          ))}
        </select>
      </div>

      {/* 4. Loan Type Filter */}
      <div>
        <h2>Loan Types</h2>
        <select
          multiple
          value={selectedFilters.loanTypeList}
          onChange={(e) => {
            const selected = Array.from(e.target.selectedOptions, (option) =>
              parseInt(option.value)
            );
            setSelectedFilters((prev) => ({ ...prev, loanTypeList: selected }));
          }}
        >
          {filterOptions.loanTypes?.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>
      </div>

      {/* 5. Tract to MSAMD Income Filter */}
      <div>
        <h2>Tract to MSAMD Income</h2>
        <input
          type="number"
          placeholder="Min income ratio"
          step="0.1"
          onChange={(e) =>
            handleNumberInput("minTractMsamdIncome", e.target.value)
          }
        />
        <input
          type="number"
          placeholder="Max income ratio"
          step="0.1"
          onChange={(e) =>
            handleNumberInput("maxTractMsamdIncome", e.target.value)
          }
        />
      </div>

      {/* 6. Loan Purpose Filter */}
      <div>
        <h2>Loan Purposes</h2>
        <select
          multiple
          value={selectedFilters.loanPurposeList}
          onChange={(e) => {
            const selected = Array.from(e.target.selectedOptions, (option) =>
              parseInt(option.value)
            );
            setSelectedFilters((prev) => ({
              ...prev,
              loanPurposeList: selected,
            }));
          }}
        >
          {filterOptions.loanPurposes?.map((purpose) => (
            <option key={purpose.id} value={purpose.id}>
              {purpose.name}
            </option>
          ))}
        </select>
      </div>

      {/* 7. Property Type Filter */}
      <div>
        <h2>Property Types</h2>
        <select
          multiple
          value={selectedFilters.propertyTypeList}
          onChange={(e) => {
            const selected = Array.from(e.target.selectedOptions, (option) =>
              parseInt(option.value)
            );
            setSelectedFilters((prev) => ({
              ...prev,
              propertyTypeList: selected,
            }));
          }}
        >
          {filterOptions.propertyTypes?.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>
      </div>

      {/* 8. Purchaser Type Filter */}
      <div>
        <h2>Purchaser Type</h2>
        <select
          value={selectedFilters.purchaserTypeFilter || ""}
          onChange={(e) =>
            setSelectedFilters((prev) => ({
              ...prev,
              purchaserTypeFilter: e.target.value || null,
            }))
          }
        >
          <option value="">Select purchaser type</option>
          <option value="Fannie Mae (FNMA)">Fannie Mae (FNMA)</option>
          <option value="Ginnie Mae (GNMA)">Ginnie Mae (GNMA)</option>
          <option value="Freddie Mac (FHLMC)">Freddie Mac (FHLMC)</option>
          <option value="Farmer Mac (FAMC)">Farmer Mac (FAMC)</option>
          <option value="Affiliate institution">Affiliate institution</option>
        </select>
      </div>

      {/* 9. Owner Occupancy Filter */}
      <div>
        <h2>Owner Occupancy</h2>
        <select
          multiple
          value={selectedFilters.ownerOccupancyList}
          onChange={(e) => {
            const selected = Array.from(e.target.selectedOptions, (option) =>
              parseInt(option.value)
            );
            setSelectedFilters((prev) => ({
              ...prev,
              ownerOccupancyList: selected,
            }));
          }}
        >
          {filterOptions.ownerOccupancyTypes?.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>
      </div>

      <div>
        
        <button onClick={updateStats}>Stats |</button>
        <button onClick={calculateRate}> Calculate Rate |</button>
        {rate && <button onClick={acceptRate}> Accept Rate</button>}
      </div>

      {stats && (
        <div>
          <h2>Current Stats:</h2>
          <p>Matching Rows: {stats.matchingRows || 0}</p>
          <p>
            Total Loan Amount: ${(stats.totalLoanAmount || 0).toLocaleString()}
          </p>
          <p>Active Filters: {stats.activeFilters || "None"}</p>
        </div>
      )}

      {rate && (
        <div>
          <h2>Calculated Rate:</h2>
          <p>Total Cost: ${(rate.totalCost || 0).toLocaleString()}</p>
          <p>Weighted Average Rate: {rate.weightedAverageRate || 0}%</p>
        </div>
      )}
    </div>
  );
}
