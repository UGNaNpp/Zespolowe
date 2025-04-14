import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [accessToken, setAccessToken] = useState(null);

    const setToken = (token) => {
        setAccessToken(token);
    };

    const removeToken = () => {
        setAccessToken(null);
    };


    return (
        <AuthContext.Provider value={{ accessToken, setToken, removeToken }}>
            {children}
        </AuthContext.Provider>
    );
};
