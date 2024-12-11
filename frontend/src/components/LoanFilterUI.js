import React, { useState, useEffect } from "react";
import { X } from "lucide-react";

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
  const [searchTerms, setSearchTerms] = useState({
    county: "",
    loanType: "",
    loanPurpose: "",
    propertyType: "",
    purchaserType: "",
    ownerOccupancy: "",
  });

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

  const deleteFilter = (filterName) => {
    fetch("http://localhost:8080/api/filters", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        filters: selectedFilters,
        filterNames: [filterName],
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to delete filter");
        return res.json();
      })
      .then((data) => {
        const newFilters = { ...selectedFilters };
        switch (filterName.toLowerCase()) {
          case "msamd":
            newFilters.msamdList = [];
            break;
          case "incomedebt":
            newFilters.minIncomeDebtRatio = null;
            newFilters.maxIncomeDebtRatio = null;
            break;
          case "county":
            newFilters.countyList = [];
            break;
          case "loantype":
            newFilters.loanTypeList = [];
            break;
          case "tractmsamdincome":
            newFilters.minTractMsamdIncome = null;
            newFilters.maxTractMsamdIncome = null;
            break;
          case "loanpurpose":
            newFilters.loanPurposeList = [];
            break;
          case "propertytype":
            newFilters.propertyTypeList = [];
            break;
          case "purchasertype":
            newFilters.purchaserTypeFilter = null;
            break;
          case "owneroccupancy":
            newFilters.ownerOccupancyList = [];
            break;
        }
        setSelectedFilters(newFilters);
        updateStats();
      })
      .catch((err) => {
        console.error("Error deleting filter:", err);
        setError(err.message);
      });
  };

  const clearAllFilters = () => {
    fetch("http://localhost:8080/api/filters/all", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedFilters),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to clear filters");
        return res.json();
      })
      .then(() => {
        setSelectedFilters({
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
        updateStats();
      })
      .catch((err) => {
        console.error("Error clearing filters:", err);
        setError(err.message);
      });
  };

  const updateStats = () => {
    fetchStats(selectedFilters);
  };

  const fetchStats = (filters) => {
    setError(null);

    try {
      // Create a copy of filters
      const filterData = { ...filters };

      // Clean up the data - remove empty arrays and null values
      Object.keys(filterData).forEach((key) => {
        if (
          filterData[key] === null ||
          (Array.isArray(filterData[key]) && filterData[key].length === 0)
        ) {
          delete filterData[key];
        }
      });

      fetch("http://localhost:8080/api/stats", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(filterData),
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
      console.error("Error processing filters:", error);
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

  const purchaserTypes = [
    "Fannie Mae (FNMA)",
    "Ginnie Mae (GNMA)",
    "Freddie Mac (FHLMC)",
    "Farmer Mac (FAMC)",
    "Affiliate institution",
  ];

  const handleCheckboxChange = (value, listName) => {
    setSelectedFilters((prev) => {
      if (listName === "purchaserTypeFilter") {
        // If clicking the same value, unselect it
        return {
          ...prev,
          [listName]: prev[listName] === value ? null : value,
        };
      }
      // For all other filters, keep the array behavior
      const currentList = prev[listName];
      const newList = currentList.includes(value)
        ? currentList.filter((item) => item !== value)
        : [...currentList, value];
      return { ...prev, [listName]: newList };
    });
  };

  const ActiveFilters = () => {
    const activeFilters = [];

    if (selectedFilters.msamdList.length > 0) {
      activeFilters.push({ name: "MSAMD", type: "msamd" });
    }
    if (
      selectedFilters.minIncomeDebtRatio ||
      selectedFilters.maxIncomeDebtRatio
    ) {
      activeFilters.push({ name: "Income/Debt Ratio", type: "incomedebt" });
    }
    if (selectedFilters.countyList.length > 0) {
      activeFilters.push({ name: "County", type: "county" });
    }
    if (selectedFilters.loanTypeList.length > 0) {
      activeFilters.push({ name: "Loan Type", type: "loantype" });
    }
    if (
      selectedFilters.minTractMsamdIncome ||
      selectedFilters.maxTractMsamdIncome
    ) {
      activeFilters.push({
        name: "Tract/MSAMD Income",
        type: "tractmsamdincome",
      });
    }
    if (selectedFilters.loanPurposeList.length > 0) {
      activeFilters.push({ name: "Loan Purpose", type: "loanpurpose" });
    }
    if (selectedFilters.propertyTypeList.length > 0) {
      activeFilters.push({ name: "Property Type", type: "propertytype" });
    }
    if (selectedFilters.purchaserTypeFilter) {
      activeFilters.push({ name: "Purchaser Type", type: "purchasertype" });
    }
    if (selectedFilters.ownerOccupancyList.length > 0) {
      activeFilters.push({ name: "Owner Occupancy", type: "owneroccupancy" });
    }

    if (activeFilters.length === 0) {
      return null;
    }

    return (
      <div className="mt-4">
        <h3 className="text-lg font-semibold mb-2">Active Filters:</h3>
        <div className="flex flex-wrap gap-2">
          {activeFilters.map((filter) => (
            <div
              key={filter.type}
              className="flex items-center bg-blue-100 rounded-lg px-3 py-1"
            >
              <span>{filter.name}</span>
              <button
                onClick={() => deleteFilter(filter.type)}
                className="ml-2 text-gray-600 hover:text-gray-800"
              >
                <X size={16} />
              </button>
            </div>
          ))}
          <button
            onClick={clearAllFilters}
            className="bg-red-100 hover:bg-red-200 text-red-700 rounded-lg px-3 py-1"
          >
            Clear All
          </button>
        </div>
      </div>
    );
  };

  const FilterSection = ({
    title,
    items,
    selectedItems,
    listName,
    searchTerm,
    showId = false,
  }) => {
    const filteredItems = items?.filter((item) => {
      const searchValue =
        (showId ? item?.name : item?.name || item)?.toString() || "";
      return searchValue
        .toLowerCase()
        .includes((searchTerm || "").toLowerCase());
    });

    return (
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">{title}</h2>
        <input
          type="text"
          placeholder={`Search ${title.toLowerCase()}...`}
          className="border rounded px-2 py-1 w-full mb-2"
          value={searchTerm}
          onChange={(e) =>
            setSearchTerms((prev) => ({
              ...prev,
              [listName.replace("List", "").toLowerCase()]: e.target.value,
            }))
          }
        />
        <div className="max-h-48 overflow-y-auto border rounded p-2">
          <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
            {filteredItems?.map((item) => {
              const value = showId ? item.id : item?.name || item;
              const label = item?.name || item?.toString() || "";
              return (
                <label
                  key={value}
                  className="flex items-center space-x-2 p-1 hover:bg-gray-50"
                >
                  <input
                    type="checkbox"
                    checked={
                      listName === "purchaserTypeFilter"
                        ? selectedItems === value
                        : selectedItems.includes(value)
                    }
                    onChange={() => handleCheckboxChange(value, listName)}
                    className="rounded"
                  />
                  <span className="text-sm">{label}</span>
                </label>
              );
            })}
          </div>
        </div>
        {Array.isArray(selectedItems) && selectedItems.length > 0 && (
          <div className="mt-2 text-sm text-gray-600">
            Selected: {selectedItems.length} {title.toLowerCase()}
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Mortgage Helper</h1>

      {error && (
        <div className="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
          Error: {error}
        </div>
      )}

      <ActiveFilters />

      {/* MSAMD Filter */}
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">MSAMD</h2>
        <input
          type="number"
          placeholder="Enter MSAMD code"
          className="border rounded px-2 py-1"
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

      {/* Income/Debt Ratio Filter */}
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">Income/Debt Ratio</h2>
        <div className="flex gap-2">
          <input
            type="number"
            placeholder="Min ratio"
            step="0.1"
            className="border rounded px-2 py-1"
            onChange={(e) =>
              handleNumberInput("minIncomeDebtRatio", e.target.value)
            }
          />
          <input
            type="number"
            placeholder="Max ratio"
            step="0.1"
            className="border rounded px-2 py-1"
            onChange={(e) =>
              handleNumberInput("maxIncomeDebtRatio", e.target.value)
            }
          />
        </div>
      </div>

      {/* Counties */}
      <FilterSection
        title="Counties"
        items={filterOptions.counties || []}
        selectedItems={selectedFilters.countyList}
        listName="countyList"
        searchTerm={searchTerms.county}
      />

      {/* Loan Types */}
      <FilterSection
        title="Loan Types"
        items={filterOptions.loanTypes || []}
        selectedItems={selectedFilters.loanTypeList}
        listName="loanTypeList"
        searchTerm={searchTerms.loanType}
        showId={true}
      />

      {/* Tract to MSAMD Income Filter */}
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">Tract to MSAMD Income</h2>
        <div className="flex gap-2">
          <input
            type="number"
            placeholder="Min income ratio"
            step="0.1"
            className="border rounded px-2 py-1"
            onChange={(e) =>
              handleNumberInput("minTractMsamdIncome", e.target.value)
            }
          />
          <input
            type="number"
            placeholder="Max income ratio"
            step="0.1"
            className="border rounded px-2 py-1"
            onChange={(e) =>
              handleNumberInput("maxTractMsamdIncome", e.target.value)
            }
          />
        </div>
      </div>

      {/* Loan Purposes */}
      <FilterSection
        title="Loan Purposes"
        items={filterOptions.loanPurposes || []}
        selectedItems={selectedFilters.loanPurposeList}
        listName="loanPurposeList"
        searchTerm={searchTerms.loanPurpose}
        showId={true}
      />

      {/* Property Types */}
      <FilterSection
        title="Property Types"
        items={filterOptions.propertyTypes || []}
        selectedItems={selectedFilters.propertyTypeList}
        listName="propertyTypeList"
        searchTerm={searchTerms.propertyType}
        showId={true}
      />

      {/* Purchaser Type */}
      <FilterSection
        title="Purchaser Type"
        items={purchaserTypes}
        selectedItems={selectedFilters.purchaserTypeFilter}
        listName="purchaserTypeFilter"
        searchTerm={searchTerms.purchaserType}
      />

      {/* Owner Occupancy */}
      <FilterSection
        title="Owner Occupancy"
        items={filterOptions.ownerOccupancyTypes || []}
        selectedItems={selectedFilters.ownerOccupancyList}
        listName="ownerOccupancyList"
        searchTerm={searchTerms.ownerOccupancy}
        showId={true}
      />

      {/* Action Buttons */}
      <div className="mt-6 space-x-2">
        <button
          onClick={updateStats}
          className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
        >
          Stats
        </button>
        <button
          onClick={calculateRate}
          className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded"
        >
          Calculate Rate
        </button>
        {rate && (
          <button
            onClick={acceptRate}
            className="bg-purple-500 hover:bg-purple-600 text-white px-4 py-2 rounded"
          >
            Accept Rate
          </button>
        )}
      </div>

      {/* Stats Display */}
      {stats && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-2">Current Stats:</h2>
          <div className="bg-gray-50 p-4 rounded-lg">
            <p className="mb-2">Matching Rows: {stats.matchingRows || 0}</p>
            <p className="mb-2">
              Total Loan Amount: $
              {(stats.totalLoanAmount || 0).toLocaleString()}
            </p>
            <p>Active Filters: {stats.activeFilters || "None"}</p>
          </div>
        </div>
      )}

      {/* Rate Display */}
      {rate && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-2">Calculated Rate:</h2>
          <div className="bg-gray-50 p-4 rounded-lg">
            <p className="mb-2">
              Total Cost: ${(rate.totalCost || 0).toLocaleString()}
            </p>
            <p>Weighted Average Rate: {rate.weightedAverageRate || 0}%</p>
          </div>
        </div>
      )}
    </div>
  );
}
