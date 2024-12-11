import React, { useState } from 'react';

const AddMortgageForm = ({ filterOptions = { loanTypes: [], counties: [] }, onMortgageAdded }) => {
  const [mortgageData, setMortgageData] = useState({
    msamd: '',
    loan_amount_000s: '',
    applicant_income_000s: '',
    loan_type: '',
    applicant_sex: '',
    applicant_ethnicity: '',
    county_name: ''
  });
  
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setMessage(null);

    const formattedData = {
      ...mortgageData,
      msamd: parseInt(mortgageData.msamd),
      loan_amount_000s: parseFloat(mortgageData.loan_amount_000s),
      applicant_income_000s: parseFloat(mortgageData.applicant_income_000s),
      loan_type: parseInt(mortgageData.loan_type),
      applicant_sex: parseInt(mortgageData.applicant_sex),
      applicant_ethnicity: parseInt(mortgageData.applicant_ethnicity),
      county_name: mortgageData.county_name
    };

    console.log('Submitting mortgage data:', formattedData);

    try {
      const response = await fetch('http://localhost:8080/api/mortgage', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formattedData)
      });

      console.log('Response status:', response.status);
      
      const data = await response.text();
      console.log('Response data:', data);

      if (!response.ok) {
        throw new Error(`Server responded with ${response.status}: ${data}`);
      }

      try {
        const jsonData = JSON.parse(data);
        setMessage(jsonData.message);
      } catch (e) {
        setMessage('Mortgage added successfully');
      }

      setMortgageData({
        msamd: '',
        loan_amount_000s: '',
        applicant_income_000s: '',
        loan_type: '',
        applicant_sex: '',
        applicant_ethnicity: '',
        county_name: ''
      });
      
      if (onMortgageAdded) {
        onMortgageAdded();
      }
    } catch (err) {
      console.error('Detailed error:', err);
      setError(`Error adding mortgage: ${err.message}`);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setMortgageData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <div className="mt-8 p-6 bg-white rounded-lg shadow">
      <h2 className="text-xl font-semibold mb-4">Apply for a Mortgage</h2>
      
      {message && (
        <div className="mb-4 p-4 bg-green-100 text-green-700 rounded">
          {message}
        </div>
      )}
      
      {error && (
        <div className="mb-4 p-4 bg-red-100 text-red-700 rounded">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">MSAMD (Metropolitan Area)</label>
            <input
              type="number"
              name="msamd"
              value={mortgageData.msamd}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
              placeholder="e.g., 35614"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">County</label>
            <select
              name="county_name"
              value={mortgageData.county_name}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
            >
              <option value="">Select County</option>
              {(filterOptions.counties || []).map(county => (
                <option key={county.name} value={county.name}>{county.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Loan Amount (in thousands)</label>
            <input
              type="number"
              step="0.1"
              name="loan_amount_000s"
              value={mortgageData.loan_amount_000s}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
              placeholder="e.g., 300 for $300,000"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Annual Income (in thousands)</label>
            <input
              type="number"
              step="0.1"
              name="applicant_income_000s"
              value={mortgageData.applicant_income_000s}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
              placeholder="e.g., 100 for $100,000"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Loan Type</label>
            <select
              name="loan_type"
              value={mortgageData.loan_type}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
            >
              <option value="">Select Loan Type</option>
              {(filterOptions.loanTypes || []).map(type => (
                <option key={type.id} value={type.id}>{type.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Sex</label>
            <select
              name="applicant_sex"
              value={mortgageData.applicant_sex}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
            >
              <option value="">Select Sex</option>
              <option value="1">Male</option>
              <option value="2">Female</option>
              <option value="3">Information not provided</option>
              <option value="4">Not applicable</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Ethnicity</label>
            <select
              name="applicant_ethnicity"
              value={mortgageData.applicant_ethnicity}
              onChange={handleInputChange}
              className="mt-1 block w-full border rounded-md shadow-sm p-2"
              required
            >
              <option value="">Select Ethnicity</option>
              <option value="1">Hispanic or Latino</option>
              <option value="2">Not Hispanic or Latino</option>
              <option value="3">Information not provided</option>
              <option value="4">Not applicable</option>
            </select>
          </div>
        </div>

        <div className="mt-6">
          <button
            type="submit"
            className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
          >
            Submit Application
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddMortgageForm;