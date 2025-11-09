import React, { useState, useRef } from "react";
import {
    View,
    Text,
    StyleSheet,
    TextInput,
    TouchableOpacity,
    Animated,
    Keyboard,
} from "react-native";
import MapView, { Polyline, Region } from "react-native-maps";
import { Ionicons } from "@expo/vector-icons";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";

type RouteData = {
    id: number;
    color: string;
    distance: string;
    origin: string;
    destination: string;
    path: { latitude: number; longitude: number }[];
};

export default function MainPage() {
    const router = useRouter();
    const mapRef = useRef<MapView>(null);
    const [region, setRegion] = useState<Region>({
        latitude: -23.55052,
        longitude: -46.633308,
        latitudeDelta: 0.05,
        longitudeDelta: 0.05,
    });

    const [query, setQuery] = useState("");
    const [routes, setRoutes] = useState<RouteData[]>([]);
    const [searched, setSearched] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [mapLocked, setMapLocked] = useState(false);
    const translateAnim = useRef(new Animated.Value(0)).current;

    const handleSearch = async () => {
        if (!query) return;
        Keyboard.dismiss();
        try {
            const res = await fetch(
                `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&q=${encodeURIComponent(
                    query
                )}`
            );
            const data = await res.json();
            if (data.length === 0) return;

            const loc = data[0];
            const latitude = parseFloat(loc.lat);
            const longitude = parseFloat(loc.lon);

            const newRegion = {
                latitude,
                longitude,
                latitudeDelta: 0.01,
                longitudeDelta: 0.01,
            };

            setRegion(newRegion);
            mapRef.current?.animateToRegion(newRegion, 1000);

            setShowConfirm(true);
        } catch (err) {
            console.error("Erro ao buscar local:", err);
        }
    };

    const handleMapDrag = () => {
        if (!mapLocked) setShowConfirm(true);
    };

    const handleViewRoutes = () => {
        setShowConfirm(false);
        setMapLocked(true);

        // 🎯 Define a nova região (zoom mais fechado)
        const zoomedRegion = {
            ...region,
            latitudeDelta: 0.008,
            longitudeDelta: 0.008,
        };

        // ✨ Anima o zoom até o nível de perimetrização
        mapRef.current?.animateToRegion(zoomedRegion, 1500);

        setTimeout(() => {
            setSearched(true);

            // 💡 Gera rotas apenas dentro do perímetro (exemplo simulado)
            const fakeRoutes: RouteData[] = [
                {
                    id: 1,
                    color: "#3A7AFE",
                    distance: "500 m",
                    origin: "Origem rota",
                    destination: "Destino rota",
                    path: [
                        { latitude: zoomedRegion.latitude - 0.002, longitude: zoomedRegion.longitude - 0.001 },
                        { latitude: zoomedRegion.latitude + 0.001, longitude: zoomedRegion.longitude + 0.002 },
                    ],
                },
                {
                    id: 2,
                    color: "#FF5555",
                    distance: "750 m",
                    origin: "Origem rota",
                    destination: "Destino rota",
                    path: [
                        { latitude: zoomedRegion.latitude - 0.001, longitude: zoomedRegion.longitude - 0.002 },
                        { latitude: zoomedRegion.latitude + 0.002, longitude: zoomedRegion.longitude + 0.003 },
                    ],
                },
            ];
            setRoutes(fakeRoutes);

            Animated.timing(translateAnim, {
                toValue: 0,
                duration: 300,
                useNativeDriver: true,
            }).start();
        }, 1200);
    };

    const handleSelectRoute = async (routeId: number) => {
        const clientData = await AsyncStorage.getItem("clientData");
        if (!clientData) {
            router.push("/Login");
            return;
        }
        alert(`Rota ${routeId} selecionada!`);
    };

    const handleBackToSelect = () => {
        setSearched(false);
        setRoutes([]);
        setMapLocked(false);
        setShowConfirm(false);

        // 🔁 Restaura o zoom inicial
        mapRef.current?.animateToRegion(
            {
                latitude: region.latitude,
                longitude: region.longitude,
                latitudeDelta: 0.05,
                longitudeDelta: 0.05,
            },
            1000
        );
    };

    return (
        <View style={styles.container}>
            {/* 🌍 Mapa */}
            <MapView
                ref={mapRef}
                style={styles.map}
                region={region}
                onRegionChangeComplete={(r) => !mapLocked && setRegion(r)}
                scrollEnabled={!mapLocked}
                zoomEnabled={!mapLocked}
                rotateEnabled={!mapLocked}
                pitchEnabled={!mapLocked}
                onPanDrag={!mapLocked ? handleMapDrag : undefined}
            >
                {searched &&
                    routes.map((r) => (
                        <Polyline key={r.id} coordinates={r.path} strokeColor={r.color} strokeWidth={4} />
                    ))}
            </MapView>

            {/* 🔙 Botão voltar */}
            {mapLocked && (
                <TouchableOpacity style={styles.backButton} onPress={handleBackToSelect}>
                    <Ionicons name="arrow-back" size={26} color="#FFF" />
                </TouchableOpacity>
            )}

            {/* 📍 Alfinete */}
            {!searched && (
                <View style={styles.pinContainer}>
                    <View style={styles.pinHead} />
                    <View style={styles.pinStick} />
                    <View style={styles.pinBase} />
                </View>
            )}

            {/* 🔍 Campo de busca */}
            {!searched && (
                <View style={styles.fixedSearchBox}>
                    <View style={styles.searchRow}>
                        <Ionicons name="search" size={20} color="#aaa" />
                        <TextInput
                            style={styles.input}
                            placeholder="Digite o endereço"
                            placeholderTextColor="#888"
                            value={query}
                            onChangeText={setQuery}
                            returnKeyType="search"
                            onSubmitEditing={handleSearch}
                        />
                        <TouchableOpacity onPress={handleSearch}>
                            <Ionicons name="arrow-forward-circle-outline" size={22} color="#999" />
                        </TouchableOpacity>
                    </View>
                </View>
            )}

            {/* ✅ Painel de confirmação */}
            {showConfirm && !searched && (
                <View style={styles.bottomConfirm}>
                    <TouchableOpacity style={styles.floatingButton} onPress={handleViewRoutes}>
                        <Ionicons name="navigate" size={18} color="#FFF" />
                        <Text style={styles.floatingButtonText}>Ver rotas</Text>
                    </TouchableOpacity>
                </View>
            )}

            {/* 📋 Rotas */}
            {searched && (
                <Animated.View style={[styles.bottomSheet, { transform: [{ translateY: translateAnim }] }]}>
                    {routes.map((r) => (
                        <View key={r.id} style={styles.routeCard}>
                            <Text style={styles.routeTitle}>
                                Rota {r.id} <Text style={{ color: r.color }}>■</Text>
                            </Text>
                            <Text style={styles.routeText}>
                                {r.origin} → {r.destination}
                            </Text>
                            <Text style={styles.routeDistance}>{r.distance}</Text>
                            <TouchableOpacity
                                style={styles.routeButton}
                                onPress={() => handleSelectRoute(r.id)}
                            >
                                <Text style={styles.routeButtonText}>Selecionar</Text>
                            </TouchableOpacity>
                        </View>
                    ))}
                </Animated.View>
            )}
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: "#0E0E10" },
    map: { flex: 1 },
    fixedSearchBox: {
        position: "absolute",
        top: 50,
        alignSelf: "center",
        backgroundColor: "#1A1A1D",
        borderRadius: 14,
        padding: 12,
        width: "90%",
        shadowColor: "#000",
        shadowOpacity: 0.4,
        shadowRadius: 6,
        elevation: 8,
        zIndex: 10,
    },
    searchRow: { flexDirection: "row", alignItems: "center", gap: 8 },
    input: { flex: 1, paddingVertical: 4, fontSize: 16, color: "#EAEAEA" },

    pinContainer: {
        position: "absolute",
        top: "45%",
        left: "50%",
        alignItems: "center",
        transform: [{ translateX: -10 }],
    },
    pinHead: {
        width: 28,
        height: 28,
        backgroundColor: "#FFF",
        borderRadius: 14,
        borderWidth: 2,
        borderColor: "#DDD",
    },
    pinStick: {
        width: 3,
        height: 18,
        backgroundColor: "#FFF",
        marginTop: -2,
    },
    pinBase: {
        width: 6,
        height: 6,
        backgroundColor: "#FFF",
        borderRadius: 3,
        marginTop: -2,
    },

    bottomConfirm: {
        position: "absolute",
        bottom: 20,
        left: 20,
        right: 20,
        alignItems: "flex-start",
    },
    confirmText: { color: "#FFF", fontSize: 15, marginBottom: 8 },

    floatingButton: {
        flexDirection: "row",
        alignItems: "center",
        gap: 6,
        backgroundColor: "#1A1A1D",
        borderRadius: 30,
        paddingVertical: 12,
        paddingHorizontal: 22,
        alignSelf: "flex-end",
        shadowColor: "#000",
        shadowOpacity: 0.3,
        shadowRadius: 3,
        elevation: 6,
    },
    floatingButtonText: { color: "#FFF", fontWeight: "bold", fontSize: 15 },

    bottomSheet: {
        position: "absolute",
        bottom: 0,
        left: 0,
        right: 0,
        backgroundColor: "#18181B",
        borderTopLeftRadius: 20,
        borderTopRightRadius: 20,
        padding: 12,
        maxHeight: "40%",
    },
    routeCard: {
        backgroundColor: "#222226",
        borderRadius: 10,
        padding: 12,
        marginVertical: 6,
    },
    routeTitle: { fontWeight: "bold", fontSize: 16, marginBottom: 4, color: "#FFF" },
    routeText: { color: "#BBB" },
    routeDistance: { marginTop: 4, color: "#777" },
    routeButton: {
        backgroundColor: "#000",
        paddingVertical: 8,
        borderRadius: 6,
        marginTop: 8,
        alignItems: "center",
    },
    routeButtonText: { color: "#fff", fontWeight: "bold", fontSize: 15 },
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
