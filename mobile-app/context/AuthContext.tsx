// ==========================================
// AuthContext.tsx — VERSÃO CORRIGIDA FINAL
// ==========================================

import React, { createContext, useContext, useState } from "react";

type Client = {
    name: string;
    email: string;
};

type AuthContextType = {
    client: Client | null;
    login: (c: Client) => void;
    logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
    client: null,
    login: () => {},
    logout: () => {},
});

export const AuthProvider = ({ children }: any) => {
    const [client, setClient] = useState<Client | null>(null);

    const login = (c: Client) => {
        // Sanitiza email logo na entrada
        const safeClient = {
            ...c,
            email: c.email.trim(),
        };

        setClient(safeClient);
    };

    const logout = () => {
        setClient(null);
    };

    return (
        <AuthContext.Provider value={{ client, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
