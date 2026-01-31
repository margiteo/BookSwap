export default function LoginPage() {
    return (
        <div className="login-container">
            <div className="login-card">
                <h1 className="title">Book Swap</h1>

                <input type="email" placeholder="Email" className="input" />
                <input type="password" placeholder="Password" className="input" />

                <button className="login-btn">Log in</button>
            </div>
        </div>
    );
}
