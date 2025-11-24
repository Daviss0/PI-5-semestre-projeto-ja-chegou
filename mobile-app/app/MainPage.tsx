// ==========================================
// MainPage.tsx — VERSÃO FINAL ABSOLUTA CORRIGIDA
// ==========================================

import React, { useState, useRef, useEffect } from "react";
import {
    View,
    Text,
    StyleSheet,
    TextInput,
    TouchableOpacity,
    ScrollView,
    Alert
} from "react-native";
import MapView, { Polyline, Marker } from "react-native-maps";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";
import { useSelectedRoute } from "../context/SelectedRouteContext";
import { API_BASE_URL } from "../config";

// CORES DAS LINHAS
const LINE_COLORS: Record<string, string> = {
    "675P-10": "#FF0000", "N634-11": "#FF5500", "607A-10": "#FF8800",
    "627M-10": "#FFCC00", "745M-10": "#00FF00", "5129-10": "#00CC66",
    "6030-10": "#00AAAA", "6062-51": "#0099FF", "6091-21": "#0066FF",
    "6091-51": "#5500FF", "N631-11": "#9900FF", "546L-10": "#CC00FF"
};

type BusInfo = { px: number; py: number; p: string; a: boolean; ta: string };

type RouteResult = {
    routeId: string;
    shortName: string;
    longName: string;
    distanceToUser: number;
    shape: number[][];
    buses: BusInfo[];
};

export default function MainPage() {

    const router = useRouter();
    const { client } = useAuth();
    const { selectedRoute, clearSelectedRoute, loadingSelectedRoute } = useSelectedRoute();

    const mapRef = useRef<MapView>(null);

    const [region, setRegion] = useState({
        latitude: -23.55052,
        longitude: -46.633308,
        latitudeDelta: 0.05,
        longitudeDelta: 0.05,
    });

    const [query, setQuery] = useState("");
    const [suggestions, setSuggestions] = useState<any[]>([]);

    const [searched, setSearched] = useState(false);
    const [mapLocked, setMapLocked] = useState(false);

    const [searchedPin, setSearchedPin] = useState<{ lat: number; lon: number } | null>(null);
    const [closestRoutes, setClosestRoutes] = useState<RouteResult[]>([]);

    let debounceTimer: any = null;
    const forceSingleRouteMode = !!selectedRoute;

    // ============================================================
    // 💾 SALVAR ROTA
    // ============================================================
    const handleSelectRoute = async (route: RouteResult) => {

        if (!client) {
            Alert.alert("Login necessário", "Você precisa estar logado.", [
                { text: "Login", onPress: () => router.push("/Login") }
            ]);
            return;
        }

        try {
            await fetch(`${API_BASE_URL}/user/routes/add`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: client.email,
                    routeId: route.routeId,
                    shortName: route.shortName,
                    longName: route.longName,
                }),
            });

            Alert.alert("OK", "Rota salva em Minhas Rotas!");

        } catch (err) {
            Alert.alert("Erro", "Não foi possível salvar a rota.");
        }
    };

    // 🔥 NÃO RENDERIZA ATÉ CONTEXTO CARREGAR
    if (loadingSelectedRoute) {
        return (
            <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#0E0E10" }}>
                <Text style={{ color: "#FFF" }}>Carregando rota...</Text>
            </View>
        );
    }

    // ============================================================
    // 🔎 SUGESTÕES VIA BACKEND
    // ============================================================
    const fetchSuggestions = (t: string) => {
        if (debounceTimer) clearTimeout(debounceTimer);
        if (t.length < 3) return setSuggestions([]);

        debounceTimer = setTimeout(async () => {
            try {
                const res = await fetch(`${API_BASE_URL}/maps/suggestions?q=${encodeURIComponent(t)}`);
                const data = await res.json();
                setSuggestions(data);
            } catch {}
        }, 120);
    };

    // ============================================================
    // 📌 SELECIONAR SUGESTÃO (SEM ALTERAR TEXTO DO INPUT!)
    // ============================================================
    const handleSelectSuggestion = (item: any) => {
        const lat = parseFloat(item.lat);
        const lon = parseFloat(item.lon);

        // ☑ NÃO alterar o input (antes isso quebrava tudo)
        // setQuery(item.display_name);  ❌ NÃO USAR

        setSuggestions([]);

        setSearchedPin({ lat, lon });
        setSearched(true);
        setMapLocked(true);

        const region = {
            latitude: lat,
            longitude: lon,
            latitudeDelta: 0.009,
            longitudeDelta: 0.009,
        };

        mapRef.current?.animateToRegion(region, 800);
        setRegion(region);

        loadClosestRoutes(lat, lon);
    };

    // ============================================================
    // ▶ BUSCA DIRETA (SEM SUGESTÕES)
    // ============================================================
    const handleSearch = async () => {
        if (!query) return;

        try {
            const res = await fetch(`${API_BASE_URL}/maps/search?q=${encodeURIComponent(query)}`);
            const data = await res.json();

            if (!data.length) {
                Alert.alert("Endereço não encontrado");
                return;
            }

            handleSelectSuggestion(data[0]);

        } catch {}
    };

    // ============================================================
    // ▶ ATUALIZAR APENAS ÔNIBUS NA ROTA SALVA
    // ============================================================
    const updateSingleRouteBuses = async () => {
        if (!closestRoutes[0]) return;

        try {
            const r = closestRoutes[0];
            const res = await fetch(`${API_BASE_URL}/olhoVivo/veiculos/${r.shortName}`);
            const dados = await res.json();

            setClosestRoutes([{ ...r, buses: dados?.vs ?? [] }]);

        } catch {}
    };

    // ============================================================
    // ▶ CARREGAR ROTAS
    // ============================================================
    const loadClosestRoutes = async (latOverride?: number, lonOverride?: number) => {

        // ⭐ MODO ROTA SALVA
        if (forceSingleRouteMode) {

            setClosestRoutes([]);

            const res = await fetch(`${API_BASE_URL}/routes/byShortName/${selectedRoute!.shortName}`);
            const route: RouteResult = await res.json();

            setClosestRoutes([route]);

            if (route.shape?.length > 0) {
                const coords = route.shape.map(([lat, lon]) => ({
                    latitude: lat,
                    longitude: lon,
                }));

                setTimeout(() => {
                    mapRef.current?.fitToCoordinates(coords, {
                        edgePadding: { top: 90, bottom: 90, left: 90, right: 90 },
                        animated: true,
                    });
                }, 400);
            }

            return;
        }

        // ⭐ MODO ROTAS PRÓXIMAS
        let lat = latOverride ?? searchedPin?.lat;
        let lon = lonOverride ?? searchedPin?.lon;
        if (!lat || !lon) return;

        try {
            const res = await fetch(`${API_BASE_URL}/routes/closest?lat=${lat}&lon=${lon}`);
            const data = await res.json();
            setClosestRoutes(data);
        } catch {}
    };

    // ============================================================
    // 🔁 CARREGAR ROTA SALVA AO ENTRAR
    // ============================================================
    useEffect(() => {
        if (forceSingleRouteMode) loadClosestRoutes();
    }, [selectedRoute]);

    // ============================================================
    // ⏱ ATUALIZAR SOMENTE ÔNIBUS DA ROTA SALVA
    // ============================================================
    useEffect(() => {
        if (!forceSingleRouteMode) return;

        updateSingleRouteBuses();
        const interval = setInterval(updateSingleRouteBuses, 2000);
        return () => clearInterval(interval);

    }, [closestRoutes.length, forceSingleRouteMode]);

    // ============================================================
    // 🔙 LIMPAR ESTADO
    // ============================================================
    const handleBack = () => {
        clearSelectedRoute();
        setClosestRoutes([]);
        setSearched(false);
        setMapLocked(false);
        setSearchedPin(null);
        setQuery("");
    };

    // ============================================================
    // 🖥 RENDERIZAÇÃO
    // ============================================================
    return (
        <View style={styles.container}>

            <MapView
                ref={mapRef}
                style={styles.map}
                region={region}
                onRegionChangeComplete={(r) => !mapLocked && setRegion(r)}
            >

                {/* SHAPES */}
                {closestRoutes.map((r, i) => (
                    <Polyline
                        key={`shape-${r.shortName}-${i}`}
                        strokeColor={LINE_COLORS[r.shortName] || "#00FF00"}
                        strokeWidth={4}
                        coordinates={r.shape.map(([lat, lon]) => ({
                            latitude: lat,
                            longitude: lon
                        }))}
                    />
                ))}

                {/* ÔNIBUS */}
                {closestRoutes.flatMap((r) =>
                    r.buses?.map((b) => (
                        <Marker
                            key={`bus-${r.shortName}-${b.p}-${b.ta}`}
                            coordinate={{ latitude: b.py, longitude: b.px }}
                        >
                            <View
                                style={{
                                    width: 22,
                                    height: 22,
                                    borderRadius: 11,
                                    backgroundColor: LINE_COLORS[r.shortName],
                                    borderWidth: 2,
                                    borderColor: "#FFF"
                                }}
                            />
                        </Marker>
                    ))
                )}

                {/* MARCADOR DO USUÁRIO */}
                {searchedPin && (
                    <Marker coordinate={{ latitude: searchedPin.lat, longitude: searchedPin.lon }}>
                        <View style={styles.searchMarker} />
                    </Marker>
                )}

            </MapView>

            {/* BOTÃO VOLTAR */}
            {(searched || forceSingleRouteMode) && (
                <TouchableOpacity style={styles.backButton} onPress={handleBack}>
                    <Ionicons name="arrow-back" size={26} color="#FFF" />
                </TouchableOpacity>
            )}

            {/* CAIXA DE BUSCA */}
            {!searched && !forceSingleRouteMode && (
                <>
                    <View style={styles.searchBox}>
                        <Ionicons name="search" size={20} color="#aaa" />
                        <TextInput
                            style={styles.searchInput}
                            placeholder="Digite o endereço"
                            placeholderTextColor="#777"
                            value={query}
                            onChangeText={(t) => {
                                setQuery(t);
                                fetchSuggestions(t);
                            }}
                            onSubmitEditing={handleSearch}
                        />
                    </View>

                    {suggestions.length > 0 && (
                        <View style={styles.suggestionContainer}>
                            {suggestions.map((item, i) => (
                                <TouchableOpacity
                                    key={i}
                                    style={styles.suggestionItem}
                                    onPress={() => handleSelectSuggestion(item)}
                                >
                                    <Text style={styles.suggestionText}>{item.display_name}</Text>
                                </TouchableOpacity>
                            ))}
                        </View>
                    )}
                </>
            )}

            {/* BOTTOM SHEET - LISTA DE ROTAS */}
            {(searched || forceSingleRouteMode) && (
                <View style={styles.bottomSheet}>
                    <View style={styles.sheetHandle} />

                    <ScrollView>
                        {closestRoutes.map((r, i) => (
                            <View key={i} style={styles.routeCard}>
                                <View style={{ flexDirection: "row", alignItems: "center", gap: 10 }}>
                                    <View style={{
                                        width: 12, height: 12,
                                        borderRadius: 6,
                                        backgroundColor: LINE_COLORS[r.shortName]
                                    }} />
                                    <Text style={styles.routeTitle}>
                                        {r.shortName} — {r.longName}
                                    </Text>
                                </View>

                                {!forceSingleRouteMode && (
                                    <TouchableOpacity
                                        style={styles.routeButton}
                                        onPress={() => handleSelectRoute(r)}
                                    >
                                        <Text style={styles.routeButtonText}>Selecionar rota</Text>
                                    </TouchableOpacity>
                                )}
                            </View>
                        ))}
                    </ScrollView>

                </View>
            )}

        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: "#0E0E10" },
    map: { flex: 1 },

    searchBox: {
        position: "absolute",
        top: 50,
        width: "90%",
        alignSelf: "center",
        backgroundColor: "#1C1C1E",
        padding: 14,
        borderRadius: 14,
        flexDirection: "row",
        zIndex: 30
    },

    searchInput: {
        flex: 1,
        color: "#FFF",
        marginLeft: 8,
        fontSize: 16
    },

    suggestionContainer: {
        position: "absolute",
        top: 110,
        width: "90%",
        alignSelf: "center",
        backgroundColor: "#1C1C1E",
        borderRadius: 14,
        zIndex: 40
    },

    suggestionItem: { padding: 10, borderBottomColor: "#333", borderBottomWidth: 1 },
    suggestionText: { color: "#EAEAEA" },

    backButton: {
        position: "absolute",
        top: 50,
        left: 20,
        padding: 10,
        borderRadius: 50,
        backgroundColor: "rgba(0,0,0,0.4)",
        zIndex: 20
    },

    bottomSheet: {
        position: "absolute",
        bottom: 0,
        left: 0,
        right: 0,
        height: "32%",
        backgroundColor: "#18181B",
        borderTopLeftRadius: 20,
        borderTopRightRadius: 20,
        paddingHorizontal: 16,
        paddingTop: 8
    },

    sheetHandle: {
        width: 50,
        height: 5,
        backgroundColor: "#555",
        borderRadius: 3,
        alignSelf: "center",
        marginBottom: 10
    },

    routeCard: {
        backgroundColor: "#222226",
        padding: 12,
        borderRadius: 12,
        marginVertical: 6
    },

    routeTitle: { color: "#FFF", fontSize: 15, fontWeight: "bold" },

    routeButton: {
        marginTop: 10,
        paddingVertical: 8,
        borderRadius: 10,
        backgroundColor: "#1A1A1D",
        alignItems: "center"
    },

    routeButtonText: { color: "#FFF", fontSize: 14, fontWeight: "bold" },

    searchMarker: {
        width: 26,
        height: 26,
        borderRadius: 13,
        backgroundColor: "#00A3FF",
        borderColor: "#FFF",
        borderWidth: 3
    }
});
