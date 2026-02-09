import './Register.css';
import { useContext, useState } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import AxiosConfig from "../../service/AxiosConfig.js";
import {API_ENDPOINTS} from "../../service/ApiEndpoints.js";
import {validateForm} from "../../util/UtilMethod.js";

const Register = () => {
    const { setAuthData } = useContext(AppContext);
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [data, setData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const onChangeHandler = (e) => {
        const name = e.target.name;
        const value = e.target.value;
        setData((data) => ({ ...data, [name]: value }));
    }

    const onSubmitHandler = async (e) => {
        e.preventDefault();

        if (!validateForm(data)) {
            return;
        }

        setLoading(true);
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.REGISTER, {
                firstName: data.firstName,
                lastName: data.lastName,
                email: data.email,
                password: data.password,
            });

            if (response.status === 201) {
                toast.success('Account created! Check your email to activate.');
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('role', response.data.role);
                setAuthData(response.data.token, response.data.role);
                setData(null)
                navigate("/login");
            }
        } catch (error) {
            console.error(error);
            toast.error('Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="register-container">
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

            <div className="register-form-section">
                <div className="register-form-container">
                    <div className="register-header">
                        <h2 className="register-title">Create Account</h2>
                        <p className="register-subtitle">Enter your information to get started</p>
                    </div>

                    <form onSubmit={onSubmitHandler} className="register-form">
                        <div className="name-row">
                            <div className="form-group">
                                <label htmlFor="firstName" className="form-label">First name</label>
                                <input
                                    type="text"
                                    name="firstName"
                                    id="firstName"
                                    placeholder="John"
                                    className="form-input"
                                    onChange={onChangeHandler}
                                    value={data.firstName}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="lastName" className="form-label">Last name</label>
                                <input
                                    type="text"
                                    name="lastName"
                                    id="lastName"
                                    placeholder="Doe"
                                    className="form-input"
                                    onChange={onChangeHandler}
                                    value={data.lastName}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="email" className="form-label">Email</label>
                            <input
                                type="email"
                                name="email"
                                id="email"
                                placeholder="john@example.com"
                                className="form-input"
                                onChange={onChangeHandler}
                                value={data.email}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password" className="form-label">Password</label>
                            <div className="password-input-wrapper">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    name="password"
                                    id="password"
                                    placeholder="Create a password"
                                    className="form-input"
                                    onChange={onChangeHandler}
                                    value={data.password}
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

                        <div className="form-group">
                            <label htmlFor="confirmPassword" className="form-label">Confirm password</label>
                            <div className="password-input-wrapper">
                                <input
                                    type={showConfirmPassword ? "text" : "password"}
                                    name="confirmPassword"
                                    id="confirmPassword"
                                    placeholder="Confirm your password"
                                    className="form-input"
                                    onChange={onChangeHandler}
                                    value={data.confirmPassword}
                                />
                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    aria-label="Toggle confirm password visibility"
                                >
                                    <i className={`bi ${showConfirmPassword ? 'bi-eye-slash' : 'bi-eye'}`}></i>
                                </button>
                            </div>
                        </div>

                        <button type="submit" className="register-btn" disabled={loading}>
                            {loading ? "Creating account..." : "Create account"}
                        </button>
                    </form>

                    <div className="signin-link">
                        Already have an account? <a href="/login">Sign in</a>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Register;