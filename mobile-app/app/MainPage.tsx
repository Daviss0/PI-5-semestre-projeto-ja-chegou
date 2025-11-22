import React, { useState, useEffect, useRef } from "react";
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    TextInput,
    Animated,
    Keyboard,
} from "react-native";
import MapView, { Polyline, Marker, Region } from "react-native-maps";
import { Ionicons } from "@expo/vector-icons";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";

const API_BASE = "http://192.168.1.104:8080/api/routes";

export default function MainPage() {
    const router = useRouter();
    const mapRef = useRef<MapView>(null);

    const [region, setRegion] = useState<Region>({
        latitude: -23.55052,
        longitude: -46.633308,
        latitudeDelta: 0.05,
        longitudeDelta: 0.05,
    });

    const [routes, setRoutes] = useState([]);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [routeShape, setRouteShape] = useState([]);
    const [truck, setTruck] = useState(null);

    const [query, setQuery] = useState("");
    const [searched, setSearched] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const translateAnim = useRef(new Animated.Value(0)).current;

    // 🔥 Carregar rotas do backend
    const fetchRoutes = async () => {
        try {
            const response = await fetch(`${API_BASE}`);
            const data = await response.json();
            setRoutes(data);
        } catch (err) {
            console.error("Erro ao carregar rotas:", err);
        }
    };

    // 🔥 Carregar shape da rota selecionada
    const fetchRouteShape = async (routeId: number) => {
        try {
            const response = await fetch(`${API_BASE}/${routeId}/shape`);
            const shape = await response.json();
            setRouteShape(shape);

            // Ajustar zoom ao shape
            if (shape.length > 0) {
                mapRef.current?.fitToCoordinates(
                    shape.map((c: any) => ({
                        latitude: c.lat,
                        longitude: c.lng,
                    })),
                    {
                        edgePadding: { top: 80, bottom: 80, left: 80, right: 80 },
                        animated: true,
                    }
                );
            }
        } catch (e) {
            console.error("Erro ao carregar shape:", e);
        }
    };

    // 🔥 Caminhão ao vivo
    const fetchTruckLive = async (codigoLinha: string) => {
        try {
            const res = await fetch(`${API_BASE}/${codigoLinha}/live/basic`);
            const data = await res.json();

            if (!data.error) {
                setTruck({
                    latitude: data.lat,
                    longitude: data.lng,
                });
            }
        } catch (err) {
            console.log("Erro caminhão ao vivo:", err);
        }
    };

    // 🔥 Atualização automática do caminhão
    useEffect(() => {
        if (!selectedRoute) return;

        const codigo = selectedRoute.codigoLinhaOlhoVivo;
        if (!codigo) return;

        fetchTruckLive(codigo);

        const interval = setInterval(() => {
            fetchTruckLive(codigo);
        }, 5000);

        return () => clearInterval(interval);
    }, [selectedRoute]);

    // 🔥 Pesquisa de endereço
    const handleSearch = async () => {
        if (!query) return;
        Keyboard.dismiss();

        try {
            const res = await fetch(
                `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&q=${encodeURIComponent(query)}`
            );

            const data = await res.json();
            if (data.length === 0) return;

            const latitude = parseFloat(data[0].lat);
            const longitude = parseFloat(data[0].lon);

            const newRegion = {
                latitude,
                longitude,
                latitudeDelta: 0.01,
                longitudeDelta: 0.01,
            };

            setRegion(newRegion);
            mapRef.current?.animateToRegion(newRegion, 1500);
            setShowConfirm(true);
        } catch (err) {
            console.error("Erro ao pesquisar:", err);
        }
    };

    // Quando clicar em "Ver rotas"
    const handleViewRoutes = () => {
        setShowConfirm(false);
        setSearched(true);
        fetchRoutes();

        Animated.timing(translateAnim, {
            toValue: 0,
            duration: 300,
            useNativeDriver: true,
        }).start();
    };

    // Selecionar rota
    const handleSelectRoute = async (route: any) => {
        setSelectedRoute(route);
        await fetchRouteShape(route.id);
    };

    const handleBackToSelect = () => {
        setSelectedRoute(null);
        setRouteShape([]);
        setTruck(null);
    };

    return (
        <View style={styles.container}>
            {/* 🌍 MAPA */}
            <MapView
                ref={mapRef}
                style={styles.map}
                region={region}
                onRegionChangeComplete={(r) => setRegion(r)}
            >
                {/* Polyline da rota real */}
                {routeShape.length > 0 && (
                    <Polyline
                        coordinates={routeShape.map((c: any) => ({
                            latitude: c.lat,
                            longitude: c.lng,
                        }))}
                        strokeWidth={5}
                        strokeColor="#00B3FF"
                    />
                )}

                {/* Caminhão ao vivo */}
                {truck && (
                    <Marker
                        coordinate={truck}
                        title="Caminhão ao vivo"
                        pinColor="green"
                    />
                )}
            </MapView>

            {/* Botão voltar para seleção */}
            {selectedRoute && (
                <TouchableOpacity style={styles.backButton} onPress={handleBackToSelect}>
                    <Ionicons name="arrow-back" size={26} color="#FFF" />
                </TouchableOpacity>
            )}

            {/* Caixa de busca */}
            {!selectedRoute && (
                <View style={styles.searchBox}>
                    <Ionicons name="search" size={20} color="#AAA" />
                    <TextInput
                        style={styles.searchInput}
                        placeholder="Digite o endereço"
                        placeholderTextColor="#777"
                        value={query}
                        onChangeText={setQuery}
                        onSubmitEditing={handleSearch}
                    />
                </View>
            )}

            {/* Botão Ver Rotas */}
            {showConfirm && !searched && (
                <TouchableOpacity style={styles.confirmButton} onPress={handleViewRoutes}>
                    <Ionicons name="navigate" size={18} color="#FFF" />
                    <Text style={styles.confirmText}>Ver rotas</Text>
                </TouchableOpacity>
            )}

            {/* Lista de rotas */}
            {searched && !selectedRoute && (
                <View style={styles.routesPanel}>
                    {routes.map((route: any) => (
                        <View key={route.id} style={styles.routeCard}>
                            <Text style={styles.routeTitle}>Rota {route.id}</Text>
                            <Text style={styles.routeText}>Código: {route.codigoLinhaOlhoVivo}</Text>

                            <TouchableOpacity
                                style={styles.routeButton}
                                onPress={() => handleSelectRoute(route)}
                            >
                                <Text style={styles.routeButtonText}>Selecionar</Text>
                            </TouchableOpacity>
                        </View>
                    ))}
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
        left: "5%",
        width: "90%",
        backgroundColor: "#1A1A1D",
        borderRadius: 12,
        padding: 12,
        flexDirection: "row",
        alignItems: "center",
        gap: 10,
    },
    searchInput: { color: "#FFF", flex: 1 },

    confirmButton: {
        position: "absolute",
        bottom: 25,
        left: 25,
        flexDirection: "row",
        alignItems: "center",
        backgroundColor: "#1A1A1D",
        paddingVertical: 12,
        paddingHorizontal: 20,
        borderRadius: 30,
    },
    confirmText: { marginLeft: 8, color: "#FFF", fontWeight: "bold" },

    routesPanel: {
        position: "absolute",
        bottom: 0,
        left: 0,
        right: 0,
        backgroundColor: "#18181B",
        padding: 10,
        maxHeight: "45%",
        borderTopLeftRadius: 24,
        borderTopRightRadius: 24,
    },
    routeCard: {
        backgroundColor: "#222226",
        padding: 12,
        borderRadius: 10,
        marginBottom: 8,
    },
    routeTitle: { color: "#FFF", fontWeight: "bold", fontSize: 16 },
    routeText: { color: "#AAA", marginTop: 4 },

    routeButton: {
        backgroundColor: "#000",
        padding: 10,
        borderRadius: 8,
        alignItems: "center",
        marginTop: 10,
    },
    routeButtonText: { color: "#FFF", fontWeight: "bold" },

    backButton: {
        position: "absolute",
        top: 50,
        left: 20,
        backgroundColor: "rgba(0,0,0,0.6)",
        borderRadius: 50,
        padding: 10,
        zIndex: 20,
    },
});
