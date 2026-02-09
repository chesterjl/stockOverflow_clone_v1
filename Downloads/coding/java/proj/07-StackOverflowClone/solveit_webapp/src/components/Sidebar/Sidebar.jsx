import { Link, useLocation, useNavigate } from "react-router-dom";
import { useContext, useState, useEffect, useRef } from "react";
import { AppContext } from "../../context/AppContext.jsx";
import './Sidebar.css'

const Sidebar = () => {
    const location = useLocation();
    const { setAuthData, clearUser, user } = useContext(AppContext);
    const navigate = useNavigate();
    const [showDropdown, setShowDropdown] = useState(false);
    const footerRef = useRef(null);

    const isActive = (path) => location.pathname === path;

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (footerRef.current && !footerRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    useEffect(() => setShowDropdown(false), [location]);

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        setAuthData(null, null);
        clearUser();
        navigate('/login');
    };

    return (
        <aside className="d-flex flex-column bg-white border-end p-3 sb-container">
            <div className="mb-4 text-center text-md-start">
                <h2 className="h4 fw-bold text-dark">
                    Solve<span className="text-primary">It</span>
                </h2>
            </div>

            <nav className="nav flex-column flex-grow-1 gap-2">
                <Link
                    className={`nav-link d-flex align-items-center rounded-5 ${isActive('/explore') ? 'active bg-light text-primary fw-semibold' : 'text-dark'}`}
                    to="/explore"
                >
                    <i className="bi bi-compass fs-4 me-2"></i> Explore
                </Link>
                <Link
                    className={`nav-link d-flex align-items-center rounded-5 ${isActive('/questions') ? 'active bg-light text-primary fw-semibold' : 'text-dark'}`}
                    to="/questions"
                >
                    <i className="bi bi-question-circle fs-4 me-2"></i> Questions
                </Link>
                <Link
                    className={`nav-link d-flex align-items-center rounded-5 ${isActive('/profile') ? 'active bg-light text-primary fw-semibold' : 'text-dark'}`}
                    to="/profile"
                >
                    <i className="bi bi-person fs-4 me-2"></i> Profile
                </Link>
            </nav>

            <div className="mt-auto position-relative" ref={footerRef}>
                {showDropdown && (
                    <div className="position-absolute bottom-100 start-0 end-0 bg-white border rounded shadow p-3 mb-2">
                        <div
                            className="dropdown-item p-0 mb-1 cursor-pointer"
                            onClick={logout}
                        >
                            Log out {user?.username.toLowerCase() || 'username'}
                        </div>
                    </div>
                )}
                <div className={`d-flex align-items-center gap-2 p-2 rounded-pill cursor-pointer ${showDropdown ? 'bg-light' : ''}`}
                    onClick={() => setShowDropdown(!showDropdown)}>

                    <i className="bi bi-person-circle fs-3 text-primary"></i>
                    <div className="flex-grow-1">
                        <div className="fw-semibold">{user?.name}</div>
                        <div className="text-muted" style={{ fontSize: '0.85rem' }}>
                            {user?.username.toLowerCase() || 'username'}
                        </div>

                    </div>

                    <i className="bi bi-three-dots fs-5 text-dark"></i>
                </div>
            </div>
        </aside>
    );
};

export default Sidebar;
