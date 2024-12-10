import logo from './logo.svg';
import './App.css';

function App() {
    const hi = "hello!";
    let nu = 2;
  return (
    <div style={{marginLeft:"20px"}}>

      <h1>
        Mortgage Helper
      </h1>

      <h3>Choose an initial option:</h3>
      
      <form onSubmit={}>
        <input type={"radio"} id={"addFilter"}></input>
        <label for={"addFilter"}> Add Filter</label> <br></br> <br></br>

        <input type={"radio"} id={"delFilter"}></input>
        <label for={"delFilter"}> Delete Filter</label> <br></br> <br></br>

        <input type={"radio"} id={"calcRate"}></input>
        <label for={"calcRate"}> Calculate Rate</label> <br></br> <br></br>

        <input type={"radio"} id={"addMortgage"}></input>
        <label for={"addMortgage"}> Add Mortgage</label> <br></br> <br></br>

        <input type={"submit"}></input>
        <input type={"reset"} id={"addMortgage"}></input>

      </form>

    </div>
  );
}

export default App;
