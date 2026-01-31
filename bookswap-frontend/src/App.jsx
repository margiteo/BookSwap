export default function App() {
    return (
        <div className="page">
            <div className="card">
                <h1 className="brand">Book Swap</h1>

                <label className="label">Email Address</label>
                <input className="input" placeholder="Email" />

                <label className="label">Password</label>
                <input className="input" placeholder="Password" type="password" />

                <a className="forgot" href="#">Forget password?</a>

                <button className="btn">Log in</button>
                <div className="registerRow">
                    <span>Nu ai cont?</span>
                    <a className="registerLink" href="#">Creează cont</a>
                </div>

            </div>
        </div>
    );
}
