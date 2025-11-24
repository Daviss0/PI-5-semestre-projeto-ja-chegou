// ==========================================
// AuthContext.tsx — FINAL TIPADO
// ==========================================

import React, { createContext, useContext, useEffect, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useSelectedRoute } from "./SelectedRouteContext";

type ClientData = {
    email: string;
    name?: string;
};

type AuthContextType = {
    client: ClientData | null;
    loading: boolean;
    login: (clientData: ClientData) => Promise<void>;
    logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType>({
    client: null,
    loading: true,
    login: async () => {},
    logout: async () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [client, setClient] = useState<ClientData | null>(null);
    const [loading, setLoading] = useState(true);

    const { clearSelectedRoute } = useSelectedRoute();

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

    const login = async (clientData: ClientData) => {
        setClient(clientData);
        await AsyncStorage.setItem("clientData", JSON.stringify(clientData));
    };

    const logout = async () => {
        setClient(null);

        await AsyncStorage.removeItem("clientData");
        await AsyncStorage.removeItem("selectedRoute");

        await clearSelectedRoute(); // LIMPA A ROTA SALVA
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
