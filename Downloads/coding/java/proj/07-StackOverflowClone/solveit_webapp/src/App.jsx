import Login from './pages/Login/Login.jsx';
import Explore from './pages/Explore/Explore.jsx';
import Register from './pages/Register/Register.jsx';
import Question from "./pages/Question/Question.jsx";
import Profile from "./pages/Profile/Profile.jsx";

import { useContext } from "react";
import { AppContext } from "./context/AppContext.jsx";
import { ToastContainer } from 'react-toastify';
import { Navigate, Route, Routes } from "react-router-dom";


// 1. Redirect to Explore if already logged in (for Login/Register pages)
const LoginRoute = ({ element, token }) => {
    return token ? <Navigate to="/explore" replace /> : element;
}

// 2. Redirect to LOGIN if NOT logged in (for Explore/Profile/Questions)
const ProtectedRoute = ({ element, token, role, allowedRoles }) => {
    if (!token) {
        return <Navigate to="/login" replace />;
    }
    if (allowedRoles && !allowedRoles.includes(role)) {
        return <Navigate to="/explore" replace />;
    }
    return element;
}

// --------------------------

const App = () => {
    const context = useContext(AppContext);
    if (!context) {
        return <div className="loading-screen">Loading...</div>;
    }

    const { auth } = context;

    return (
        <div>
            <ToastContainer />
            <Routes>
                {/* Notice we pass auth.token and auth.role as props now */}
                <Route path="/login" element={<LoginRoute token={auth.token} element={<Login />} />} />
                <Route path="/register" element={<LoginRoute token={auth.token} element={<Register />} />} />

                <Route path="/" element={<ProtectedRoute token={auth.token} role={auth.role} element={<Explore />} />} />
                <Route path="/explore" element={<ProtectedRoute token={auth.token} role={auth.role} element={<Explore />} />} />
                <Route path="/questions" element={<ProtectedRoute token={auth.token} role={auth.role} element={<Question />} />} />
                <Route path="/profile" element={<ProtectedRoute token={auth.token} role={auth.role} element={<Profile />} />} />

                <Route path="*" element={<Navigate to={auth.token ? "/explore" : "/login"} replace />} />
            </Routes>
        </div>
    )
}

export default App;