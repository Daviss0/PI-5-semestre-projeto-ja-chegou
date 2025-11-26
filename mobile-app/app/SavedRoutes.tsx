// ==========================================
// SavedRoutes.tsx — UI no padrão da tela de Notificações
// ==========================================

import React, { useState } from "react";
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Alert,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useAuth } from "../context/AuthContext";
import { useSelectedRoute } from "../context/SelectedRouteContext";
import { useRouter, useFocusEffect } from "expo-router";
import { API_BASE_URL } from "../config";

type UserRoute = {
    id: number;
    routeId: string;
    shortName: string;
    longName: string;
    baseLat?: number;
    baseLon?: number;
};

export default function SavedRoutes() {
    const { client } = useAuth();
    const { setSelectedRoute } = useSelectedRoute();
    const router = useRouter();

    const [routes, setRoutes] = useState<UserRoute[]>([]);

    const loadSavedRoutes = async () => {
        if (!client) {
            setRoutes([]);
            return;
        }

        const cleanEmail = client.email.trim();
        const res = await fetch(`${API_BASE_URL}/user/routes/${cleanEmail}`);
        const data = await res.json();
        setRoutes(data);
    };

    useFocusEffect(
        React.useCallback(() => {
            loadSavedRoutes();
        }, [client])
    );

    const deleteRoute = (id: number) => {
        Alert.alert("Excluir rota", "Deseja remover esta rota?", [
            { text: "Cancelar" },
            {
                text: "Excluir",
                onPress: async () => {
                    await fetch(`${API_BASE_URL}/user/routes/delete/${id}`, {
                        method: "DELETE",
                    });
                    loadSavedRoutes();
                },
            },
        ]);
    };

    const handleViewRoute = async (route: UserRoute) => {
        await setSelectedRoute({
            routeId: route.routeId,
            shortName: route.shortName,
            longName: route.longName,
            baseLat: route.baseLat ?? null,
            baseLon: route.baseLon ?? null,
        });

        router.push("/MainPage");
    };

    if (!client) {
        return (
            <View style={styles.centerContainer}>
                <Text style={styles.notLoggedText}>
                    Faça login para ver suas rotas salvas.
                </Text>
            </View>
        );
    }

    return (
        <View style={styles.wrapper}>
            {/* Botão Voltar */}
            <TouchableOpacity style={styles.backButton} onPress={() => router.push("/Profile")}>
                <Ionicons name="arrow-back" size={28} color="#FFF" />
            </TouchableOpacity>

            {/* Título */}
            <Text style={styles.title}>Minhas Rotas Salvas</Text>

            <ScrollView style={{ marginTop: 10 }}>
                {routes.map((r) => (
                    <View key={r.id} style={styles.card}>
                        <View style={styles.cardHeader}>
                            <Text style={styles.cardTitle}>
                                {r.shortName} — {r.longName}
                            </Text>

                            <TouchableOpacity onPress={() => deleteRoute(r.id)}>
                                <Ionicons name="trash-outline" size={22} color="#FF5555" />
                            </TouchableOpacity>
                        </View>

                        <TouchableOpacity
                            style={styles.button}
                            onPress={() => handleViewRoute(r)}
                        >
                            <Text style={styles.buttonText}>Ver rota</Text>
                        </TouchableOpacity>
                    </View>
                ))}
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        backgroundColor: "#0E0E10",
        paddingTop: 90,
        paddingHorizontal: 20,
    },

    backButton: {
        position: "absolute",
        top: 50,
        left: 20,
        padding: 6,
        zIndex: 20,
    },

    title: {
        color: "#FFF",
        fontSize: 26,
        fontWeight: "bold",
        textAlign: "center",
        marginBottom: 20,
    },

    card: {
        backgroundColor: "#1A1A1D",
        padding: 16,
        borderRadius: 14,
        marginBottom: 16,
        borderColor: "#333",
        borderWidth: 1,
    },

    cardHeader: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: 14,
    },

    cardTitle: {
        color: "#FFF",
        fontSize: 16,
        fontWeight: "bold",
        flex: 1,
        paddingRight: 8,
    },

    button: {
        backgroundColor: "#FFF",
        paddingVertical: 12,
        borderRadius: 12,
        alignItems: "center",
    },

    buttonText: {
        color: "#000",
        fontWeight: "bold",
        fontSize: 16,
    },

    centerContainer: {
        flex: 1,
        backgroundColor: "#0E0E10",
        justifyContent: "center",
        alignItems: "center",
    },

    notLoggedText: {
        color: "#FFF",
        fontSize: 18,
        textAlign: "center",
    },
});
