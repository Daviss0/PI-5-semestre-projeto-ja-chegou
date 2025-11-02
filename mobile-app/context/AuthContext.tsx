import React, { createContext, useContext, useState, useEffect } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

type User = {
    id?: number;
    name?: string;
    email?: string;
    cpf?: string;
    phone?: string;
    birthDate?: string;
    cep?: string;
    logradouro?: string;
    hood?: string;
    city?: string;
    state?: string;
    number?: string;
    complement?: string;
};

type AuthContextType = {
    user: User | null;
    loading: boolean;
    login: (userData: User) => Promise<void>;
    logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType>({
    user: null,
    loading: true,
    login: async () => {},
    logout: async () => {},
});

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        (async () => {
            try {
                const stored = await AsyncStorage.getItem("clientData");
                if (stored) setUser(JSON.parse(stored));
            } catch (err) {
                console.error("Erro ao carregar sessão:", err);
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const login = async (userData: User) => {
        await AsyncStorage.setItem("clientData", JSON.stringify(userData));
        setUser(userData);
    };

    const logout = async () => {
        await AsyncStorage.removeItem("clientData");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
