// context/AuthContext.tsx
import React, { createContext, useContext, useEffect, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

type AuthContextType = {
    client: any | null;
    loading: boolean;
    login: (clientData: any) => Promise<void>;
    logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType>({
    client: null,
    loading: true,
    login: async () => {},
    logout: async () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [client, setClient] = useState<any | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        (async () => {
            try {
                const stored = await AsyncStorage.getItem("clientData");
                if (stored) {
                    setClient(JSON.parse(stored));
                }
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const login = async (clientData: any) => {
        setClient(clientData);
        await AsyncStorage.setItem("clientData", JSON.stringify(clientData));
    };

    const logout = async () => {
        setClient(null);
        await AsyncStorage.removeItem("clientData");
    };

    return (
        <AuthContext.Provider value={{ client, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
