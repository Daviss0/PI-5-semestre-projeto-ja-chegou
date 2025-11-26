// ==========================================
// SelectedRouteContext.tsx — VERSÃO COM BASELAT / BASELON
// ==========================================

import React, { createContext, useContext, useState, useEffect } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

type SelectedRoute = {
    routeId: string;
    shortName: string;
    longName: string;
    baseLat?: number | null;
    baseLon?: number | null;
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

    // Carrega rota salva do AsyncStorage ao iniciar o app
    useEffect(() => {
        let isMounted = true;

        (async () => {
            try {
                const stored = await AsyncStorage.getItem("selectedRoute");

                console.log("🔵 Valor carregado do AsyncStorage:", stored);

                if (stored && isMounted) {
                    const parsed = JSON.parse(stored);
                    console.log("🟢 JSON.parse(stored):", parsed);

                    setSelectedRouteState(parsed);
                } else {
                    console.log("🟡 Nenhum selectedRoute salvo no AsyncStorage.");
                }

            } catch (err) {
                console.log("❌ Erro ao carregar selectedRoute:", err);
            } finally {
                if (isMounted) setLoading(false);
            }
        })();

        return () => { isMounted = false; };
    }, []);

    const setSelectedRoute = async (route: SelectedRoute | null) => {
        console.log("🟦 setSelectedRoute CHAMADO com:", route);   // <--- AQUI
        try {
            setSelectedRouteState(route);

            if (route) {
                await AsyncStorage.setItem("selectedRoute", JSON.stringify(route));
                console.log("🟩 SALVO NO ASYNC:", route);        // <--- AQUI
            } else {
                await AsyncStorage.removeItem("selectedRoute");
            }
        } catch (err) {
            console.log("Erro ao salvar selectedRoute:", err);
        }
    };

    const clearSelectedRoute = async () => {
        try {
            setSelectedRouteState(null);
            await AsyncStorage.removeItem("selectedRoute");
        } catch (err) {
            console.log("Erro ao limpar selectedRoute:", err);
        }
    };

    return (
        <SelectedRouteContext.Provider
            value={{
                selectedRoute,
                setSelectedRoute,
                clearSelectedRoute,
                loadingSelectedRoute
            }}
        >
            {children}
        </SelectedRouteContext.Provider>
    );
}

export function useSelectedRoute() {
    return useContext(SelectedRouteContext);
}
