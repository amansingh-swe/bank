// Frontend: App.jsx
import { useState } from "react";
import axios from "axios";
import "./App.css";

export default function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [initialBalance, setInitialBalance] = useState("");
  const [token, setToken] = useState("");
  const [amount, setAmount] = useState("");
  const [balance, setBalance] = useState(null);
  const [message, setMessage] = useState("");
  const [httpResponse, setHttpResponse] = useState("");

  const api = axios.create({ baseURL: "http://localhost:8080/api" });

  const register = async () => {
    try {
      const res = await api.post("/register", null, {
        params: { username, password, initial_balance: initialBalance },
      });
      setMessage(res.data);
      setHttpResponse("HTTP " + res.status + " " + res.statusText);
    } catch (e) {
      setMessage("Invalid Input.");
      setHttpResponse(e.response ? "HTTP " + e.response.status + " " + e.response.statusText : "No response");
    }
  };

  const login = async () => {
    try {
      const res = await api.post("/login", null, {
        params: { username, password },
      });
      setToken(res.data);
      setMessage("Login successful.");
      setHttpResponse("HTTP " + res.status + " " + res.statusText);
    } catch (e) {
      setMessage("Login failed.");
      setHttpResponse(e.response ? "HTTP " + e.response.status + " " + e.response.statusText : "No response");
    }
  };

  const deposit = async () => {
    try {
      const res = await api.post("/deposit", null, {
        params: { token, amount },
      });
      setBalance(res.data);
      setMessage("Deposit successful.");
      setHttpResponse("HTTP " + res.status + " " + res.statusText);
    } catch (e) {
      setMessage("Deposit failed.");
      setHttpResponse(e.response ? "HTTP " + e.response.status + " " + e.response.statusText : "No response");
    }
  };

  const withdraw = async () => {
    try {
      const res = await api.post("/withdraw", null, {
        params: { token, amount },
      });
      setBalance(res.data);
      setMessage("Withdrawal processed.");
      setHttpResponse("HTTP " + res.status + " " + res.statusText);
    } catch (e) {
      setMessage("Withdrawal failed.");
      setHttpResponse(e.response ? "HTTP " + e.response.status + " " + e.response.statusText : "No response");
    }
  };

  const checkBalance = async () => {
    try {
      const res = await api.get("/balance", { params: { token } });
      setBalance(res.data);
      setHttpResponse("HTTP " + res.status + " " + res.statusText);
    } catch (e) {
      setMessage("Balance check failed. Unauthorized user");
      setHttpResponse(e.response ? "HTTP " + e.response.status + " " + e.response.statusText : "No response");
    }
  };

  const logout = () => {
    setToken("");
    setUsername("");
    setPassword("");
    setAmount("");
    setBalance(null);
    setMessage("Logged out.");
    setHttpResponse("");
  };

  return (
    <div className="app-container">
      <div className="card">
        <h1 className="title">Bank App</h1>

        <div className="form-group">
          <input placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} className="input" />
          <input placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} className="input" />
          <input placeholder="Initial Balance (e.g., 100.00)" value={initialBalance} onChange={(e) => setInitialBalance(e.target.value)} className="input" />
          <div className="button-row">
            <button onClick={register} className="btn btn-blue">Register</button>
            <button onClick={login} className="btn btn-green">Login</button>
            <button onClick={logout} className="btn btn-gray">Logout</button>
          </div>
        </div>

        <hr />

        <div className="form-group">
          <input placeholder="Amount" value={amount} onChange={(e) => setAmount(e.target.value)} className="input" />
          <div className="button-row">
            <button onClick={deposit} className="btn btn-yellow">Deposit</button>
            <button onClick={withdraw} className="btn btn-red">Withdraw</button>
            <button onClick={checkBalance} className="btn btn-gray">Check Balance</button>
          </div>
        </div>

        {balance && <div className="output">Balance: ${balance}</div>}
        {message && <div className="message">{message}</div>}
        {httpResponse && <div className="http-response">{httpResponse}</div>}
      </div>
    </div>
  );
}
