import React, { useState, useEffect, useRef } from "react";
import {
    View,
    Text,
    StyleSheet,
    TextInput,
    TouchableOpacity,
    FlatList,
    Keyboard,
    Animated,
    Platform,
} from "react-native";
import MapView, { Marker, Polyline } from "react-native-maps";
import { Ionicons } from "@expo/vector-icons";

type RouteData = {
    id: number;
    color: string;
    distance: string;
    origin: string;
    destination: string;
    path: { latitude: number; longitude: number }[];
};

export default function MainPage() {
    const mapRef = useRef<MapView>(null);
    const [region, setRegion] = useState({
        latitude: -23.55052,
        longitude: -46.633308,
        latitudeDelta: 0.05,
        longitudeDelta: 0.05,
    });

    const [query, setQuery] = useState("");
    const [address, setAddress] = useState<string | null>(null);
    const [searched, setSearched] = useState(false);
    const [routes, setRoutes] = useState<RouteData[]>([]);
    const [userLocation, setUserLocation] = useState<{ latitude: number; longitude: number } | null>(
        null
    );

    // controla a visibilidade do painel
    const [isVisible, setIsVisible] = useState(true);
    const fadeAnim = useRef(new Animated.Value(1)).current; // opacidade
    const translateAnim = useRef(new Animated.Value(0)).current; // movimento vertical

    const toggleRoutes = () => {
        const newVisible = !isVisible;
        setIsVisible(newVisible);

        Animated.parallel([
            Animated.timing(fadeAnim, {
                toValue: newVisible ? 1 : 0,
                duration: 350,
                useNativeDriver: true,
            }),
            Animated.timing(translateAnim, {
                toValue: newVisible ? 0 : 100,
                duration: 350,
                useNativeDriver: true,
            }),
        ]).start();
    };

    // anima a caixa de busca quando o teclado aparece
    const [searchBoxY] = useState(new Animated.Value(50));
    useEffect(() => {
        const showSub = Keyboard.addListener("keyboardWillShow", () => {
            Animated.timing(searchBoxY, {
                toValue: Platform.OS === "ios" ? 10 : 0,
                duration: 250,
                useNativeDriver: false,
            }).start();
        });
        const hideSub = Keyboard.addListener("keyboardWillHide", () => {
            Animated.timing(searchBoxY, {
                toValue: 50,
                duration: 250,
                useNativeDriver: false,
            }).start();
        });
        return () => {
            showSub.remove();
            hideSub.remove();
        };
    }, []);

    // busca por endereço (OpenStreetMap)
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
            if (data.length === 0) {
                setAddress("Endereço não encontrado");
                setSearched(false);
                return;
            }

            const loc = data[0];
            const addr = loc.address;
            const street = addr.road || addr.pedestrian || addr.suburb || "Endereço";
            const bairro = addr.suburb || addr.neighbourhood || "";
            const cep = addr.postcode ? ` - ${addr.postcode}` : "";

            const latitude = parseFloat(loc.lat);
            const longitude = parseFloat(loc.lon);

            setAddress(`${street}, ${bairro}${cep}`);
            setUserLocation({ latitude, longitude });

            // centraliza o mapa
            mapRef.current?.animateToRegion(
                {
                    latitude: latitude - 0.002,
                    longitude,
                    latitudeDelta: 0.008,
                    longitudeDelta: 0.008,
                },
                1000
            );

            setRegion({
                latitude: latitude - 0.002,
                longitude,
                latitudeDelta: 0.008,
                longitudeDelta: 0.008,
            });

            // cria rotas simuladas
            const fakeRoutes: RouteData[] = [
                {
                    id: 1,
                    color: "#3A7AFE",
                    distance: "500 m",
                    origin: "Origem rota",
                    destination: "Destino rota",
                    path: [
                        { latitude: latitude - 0.003, longitude: longitude - 0.002 },
                        { latitude: latitude - 0.001, longitude: longitude + 0.001 },
                    ],
                },
                {
                    id: 2,
                    color: "#FF5555",
                    distance: "750 m",
                    origin: "Origem rota",
                    destination: "Destino rota",
                    path: [
                        { latitude: latitude - 0.002, longitude: longitude - 0.003 },
                        { latitude: latitude + 0.001, longitude: longitude + 0.002 },
                    ],
                },
                {
                    id: 3,
                    color: "#FFB020",
                    distance: "600 m",
                    origin: "Origem rota",
                    destination: "Destino rota",
                    path: [
                        { latitude: latitude - 0.001, longitude: longitude - 0.001 },
                        { latitude: latitude + 0.002, longitude: longitude + 0.001 },
                    ],
                },
            ];
            setRoutes(fakeRoutes);
            setSearched(true);
        } catch (err) {
            console.error("Erro ao buscar local:", err);
            setAddress("Erro ao buscar endereço");
        }
    };

    const handleClearSearch = () => {
        setQuery("");
        setAddress(null);
        setRoutes([]);
        setSearched(false);
        setUserLocation(null);
        Keyboard.dismiss();
        setRegion({
            latitude: -23.55052,
            longitude: -46.633308,
            latitudeDelta: 0.05,
            longitudeDelta: 0.05,
        });
    };

    const renderRoute = ({ item }: { item: RouteData }) => (
        <View style={styles.routeCard}>
            <Text style={styles.routeTitle}>
                Rota {item.id} <Text style={{ color: item.color }}>■</Text>
            </Text>
            <Text style={styles.routeText}>
                {item.origin} → {item.destination}
            </Text>
            <Text style={styles.routeDistance}>{item.distance}</Text>
            <TouchableOpacity style={styles.routeButton}>
                <Text style={styles.routeButtonText}>Selecionar</Text>
            </TouchableOpacity>
        </View>
    );

    return (
        <View style={styles.container}>
            <MapView
                ref={mapRef}
                style={styles.map}
                region={region}
                onPress={toggleRoutes} // 👈 clique no mapa para ocultar ou mostrar o painel
            >
                {searched &&
                    routes.map((r) => (
                        <Polyline key={r.id} coordinates={r.path} strokeColor={r.color} strokeWidth={4} />
                    ))}
                {userLocation && (
                    <Marker coordinate={userLocation} title="Local encontrado" pinColor="#3A7AFE" />
                )}
            </MapView>

            {/* 🔍 Caixa de busca */}
            <View style={styles.fixedSearchBox}>
                <View style={styles.searchRow}>
                    <Ionicons name="search" size={20} color="#aaa" />
                    <TextInput
                        style={styles.input}
                        placeholder="Digite o endereço"
                        placeholderTextColor="#888"
                        value={query}
                        keyboardType="default"
                        returnKeyType="search"
                        onSubmitEditing={handleSearch}
                        onChangeText={setQuery}
                    />
                    {query.length > 0 && (
                        <TouchableOpacity onPress={handleClearSearch}>
                            <Ionicons name="close-circle" size={20} color="#999" />
                        </TouchableOpacity>
                    )}
                    <TouchableOpacity onPress={handleSearch}>
                        <Ionicons name="arrow-forward-circle-outline" size={22} color="#999" />
                    </TouchableOpacity>
                </View>
                {searched && <Text style={styles.addressText}>{address}</Text>}
            </View>

            {/* 📋 Painel de rotas animado */}
            {searched && (
                <Animated.View
                    style={[
                        styles.bottomSheet,
                        {
                            opacity: fadeAnim,
                            transform: [{ translateY: translateAnim }],
                        },
                    ]}
                    pointerEvents={isVisible ? "auto" : "none"} // bloqueia interação quando invisível
                >
                    <FlatList
                        data={routes}
                        keyExtractor={(i) => i.id.toString()}
                        renderItem={renderRoute}
                        keyboardShouldPersistTaps="handled"
                        showsVerticalScrollIndicator={false}
                    />
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
    addressText: { color: "#888", marginTop: 4, fontStyle: "italic" },
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
        shadowColor: "#000",
        shadowOpacity: 0.3,
        shadowRadius: 10,
        elevation: 10,
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
});
