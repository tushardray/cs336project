import React, { useState } from "react";
import { useNavigate } from "react-router-dom";


function HomepageForm() {
    const [selectedOption, setSelectedOption] = useState("");
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        
        if (selectedOption === "addFilter") {
                navigate("/AddFilter");
          } else if (selectedOption === "delFilter") {
                navigate("/DelFilter");
          } else if (selectedOption === "calcRate") {
                navigate("/CalcRate");
          } else if (selectedOption === "addMortgage") {
                navigate("/AddMortgage");
          } else {
            alert("Please select an option before submitting.");
          }
    }


    return (
        <div>
            <h1>Mortgage Helper</h1>

            <form onSubmit={handleSubmit}>
                <label>
                    <input
                        type={"radio"}
                        id={"addFilter"}
                        value={"addFilter"}
                        onChange={(e) => setSelectedOption(e.target.value)}
                    />
                    Add Filter <br/> <br/>
                </label>

                <label>
                    <input
                        type={"radio"}
                        id={"delFilter"}
                        value={"delFilter"}
                        onChange={(e) => setSelectedOption(e.target.value)}
                    />
                    Delete Filter <br/> <br/>
                </label>

                <label>
                    <input
                        type={"radio"}
                        id={"calcRate"}
                        value={"calcRate"}
                        onChange={(e) => setSelectedOption(e.target.value)}
                    />
                    Calculate Rate <br/> <br/>
                </label>

                <label>
                    <input
                        type={"radio"}
                        id={"addMortgage"}
                        value={"addMortgage"}
                        onChange={(e) => setSelectedOption(e.target.value)}
                    />
                    Add Mortgage <br/> <br/>
                </label>

                <input type={"submit"}/>
                <input type={"reset"}/>
            </form>
        </div>
    );
}

export default HomepageForm;
