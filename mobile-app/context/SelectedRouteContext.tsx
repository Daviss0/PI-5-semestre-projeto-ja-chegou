// ==========================================
// SelectedRouteContext.tsx — FINAL TIPADO
// ==========================================

import React, { createContext, useContext, useState, useEffect } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

export type SelectedRoute = {
    routeId: string;
    shortName: string;
    longName: string;
};

type SelectedRouteContextType = {
    selectedRoute: SelectedRoute | null;
    setSelectedRoute: (route: SelectedRoute | null) => Promise<void>;
    clearSelectedRoute: () => Promise<void>;
    loadingSelectedRoute: boolean;
};

const SelectedRouteContext = createContext<SelectedRouteContextType>({
    selectedRoute: null,
    setSelectedRoute: async () => {},
    clearSelectedRoute: async () => {},
    loadingSelectedRoute: true,
});

export function SelectedRouteProvider({ children }: { children: React.ReactNode }) {

    const [selectedRoute, setSelectedRouteState] = useState<SelectedRoute | null>(null);
    const [loadingSelectedRoute, setLoading] = useState(true);

    // Carrega rota salva
    useEffect(() => {
        (async () => {
            try {
                const stored = await AsyncStorage.getItem("selectedRoute");
                if (stored) {
                    setSelectedRouteState(JSON.parse(stored));
                }
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const setSelectedRoute = async (route: SelectedRoute | null) => {
        setSelectedRouteState(route);

        if (route) {
            await AsyncStorage.setItem("selectedRoute", JSON.stringify(route));
        } else {
            await AsyncStorage.removeItem("selectedRoute");
        }
    };

    const clearSelectedRoute = async () => {
        setSelectedRouteState(null);
        await AsyncStorage.removeItem("selectedRoute");
    };

    return (
        <SelectedRouteContext.Provider
            value={{
                selectedRoute,
                setSelectedRoute,
                clearSelectedRoute,
                loadingSelectedRoute,
            }}
        >
            {children}
        </SelectedRouteContext.Provider>
    );
}

export function useSelectedRoute() {
    return useContext(SelectedRouteContext);
}
