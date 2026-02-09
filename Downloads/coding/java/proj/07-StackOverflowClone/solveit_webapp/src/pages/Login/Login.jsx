import './Login.css';
import { useContext, useState } from 'react';
import { toast } from 'react-toastify';
import { API_ENDPOINTS } from '../../service/ApiEndpoints.js';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import AxiosConfig from "../../service/AxiosConfig.js";

const Login = () => {
    const { setAuthData } = useContext(AppContext);
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [data, setData] = useState({
        email: '',
        password: '',
    });

    const onChangeHandler = (e) => {
        const name = e.target.name;
        const value = e.target.value;
        setData((data) => ({ ...data, [name]: value }));
    }

    const onSubmitHandler = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.LOGIN, data);
            console.log(response);
            if (response.status === 200) {
                toast.success('Login successful');

                // Save token and role
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('role', response.data.user.role);
                setAuthData(response.data.token, response.data.user.role);

                navigate("/dashboard");
            }
        } catch (error) {
            console.error(error);
            toast.error('Login failed: invalid credentials.');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-container">
            {/* Left Side - Branding */}
            <div className="login-branding">
                <div className="branding-content">
                    <div className="logo-section">
                        <h1 className="logo-text">Solve<span className="logo-highlight">It</span></h1>
                    </div>
                    <ul className="list-unstyled lead fs-6 opacity-75 mt-4">
                        <li className="mb-3"><i className="bi bi-tags-fill me-2 text-primary"></i> Get unstuck — ask a question</li>
                        <li className="mb-3"><i className="bi bi-trophy-fill me-2 text-warning"></i> Unlock badges and earn reputation</li>
                        <li className="mb-3"><i className="bi bi-people-fill me-2 text-success"></i> Save your favorite tags and filters</li>
                    </ul>
                    <p className="branding-description">
                        Join solveit community and talk with other developers.
                    </p>
                </div>
            </div>

            {/* Right Side - Login Form */}
            <div className="login-form-section">
                <div className="login-form-container">
                    <div className="login-header">
                        <h2 className="login-title">Welcome back!</h2>
                        <p className="login-subtitle">Log in to access your solveit account.</p>
                    </div>

                    <form onSubmit={onSubmitHandler} className="login-form">
                        <div className="form-group">
                            <label htmlFor="email" className="form-label">Email</label>
                            <input
                                type="text"
                                name="email"
                                id="email"
                                placeholder="john@example.com"
                                className="form-input"
                                onChange={onChangeHandler}
                                value={data.email}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password" className="form-label">Password</label>
                            <div className="password-input-wrapper">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    name="password"
                                    id="password"
                                    placeholder="Enter your password"
                                    className="form-input"
                                    onChange={onChangeHandler}
                                    value={data.password}
                                    required
                                />
                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() => setShowPassword(!showPassword)}
                                    aria-label="Toggle password visibility"
                                >
                                    <i className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`}></i>
                                </button>
                            </div>
                        </div>
                        <button type="submit" className="login-btn" disabled={loading}>
                            {loading ? "Logging in..." : "Log in"}
                        </button>

                    </form>

                    <div className="signup-link">
                        Don't have an account? <a href="/register">Sign up here for free</a>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;