import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomepageForm from "./HomepageForm";
import AddFilter from "./AddFilter";
import DelFilter from "./DelFilter";
import CalcRate from "./CalcRate";
import AddMortgage from "./AddMortgage";





function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomepageForm />} />
        <Route path="/AddFilter" element={<AddFilter />} />
        <Route path="/DelFilter" element={<DelFilter />} />
        <Route path="/CalcRate" element={<CalcRate/>} />
        <Route path="/AddMortgage" element={<AddMortgage />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
