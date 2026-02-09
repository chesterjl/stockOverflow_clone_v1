import { createContext, useEffect, useState, useCallback } from "react"
import AxiosConfig from "../service/AxiosConfig.js";
import { API_ENDPOINTS } from "../service/ApiEndpoints.js";

export const AppContext = createContext(null);

export const AppContextProvider = (props) => {
    const [auth, setAuth] = useState({
        token: localStorage.getItem('token'),
        role: localStorage.getItem('role')
    });

    const [user, setUser] = useState(null);

    const clearUser = useCallback(() => {
        setUser(null);
        setAuth({ token: null, role: null });
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('user');
    }, []);

    const setAuthData = useCallback((token, role) => {
        setAuth({ token, role });
        if (token) localStorage.setItem('token', token);
        if (role) localStorage.setItem('role', role);
    }, []);

    useEffect(() => {
        async function loadUserInfo() {
            if (!auth.token) return;

            try {
                const response = await AxiosConfig.get(API_ENDPOINTS.GET_USER_INFO);
                if (response.status === 200) {
                    setUser(response.data.user);
                    localStorage.setItem('user', JSON.stringify(response.data.user));
                }
            } catch (error) {
                console.error("Failed to fetch user info:", error);
                if (error.response?.status === 401) {
                    clearUser();
                }
            }
        }

        loadUserInfo();
    }, [auth.token, clearUser]);

    const contextValue = {
        auth,
        user,
        setAuthData,
        clearUser
    }

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}