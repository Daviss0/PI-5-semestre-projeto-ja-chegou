// ==========================================
// SavedRoutes.tsx — VERSÃO FINAL
// ==========================================

import React, { useEffect, useState } from "react";
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Alert } from "react-native";
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
};

export default function SavedRoutes() {

    const { client } = useAuth();
    const { setSelectedRoute } = useSelectedRoute();
    const router = useRouter();

    const [routes, setRoutes] = useState<UserRoute[]>([]);

    // Carregar rotas SOMENTE se houver login
    const loadSavedRoutes = async () => {
        if (!client) {
            setRoutes([]);
            return;
        }

        const res = await fetch(`${API_BASE_URL}/user/routes/${client.email}`);
        const data = await res.json();
        setRoutes(data);
    };

    // Atualiza lista ao entrar na tela
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
        });

        router.push("/MainPage");
    };

    // Tela para usuário deslogado
    if (!client) {
        return (
            <View style={styles.container}>
                <Text style={styles.notLoggedText}>Faça login para ver suas rotas salvas.</Text>
            </View>
        );
    }

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Minhas Rotas Salvas</Text>

            <ScrollView style={{ marginTop: 20 }}>
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

                        <TouchableOpacity style={styles.button} onPress={() => handleViewRoute(r)}>
                            <Text style={styles.buttonText}>Ver rota</Text>
                        </TouchableOpacity>

                    </View>
                ))}
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: "#0E0E10", paddingTop: 50, paddingHorizontal: 20 },
    title: { color: "#FFF", fontSize: 22, fontWeight: "bold" },

    notLoggedText: {
        marginTop: 60,
        color: "#FFF",
        fontSize: 18,
        textAlign: "center",
    },

    card: {
        backgroundColor: "#1A1A1D",
        padding: 16,
        borderRadius: 14,
        marginBottom: 14,
    },
    cardHeader: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
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
        marginTop: 12,
        paddingVertical: 10,
        borderRadius: 10,
        alignItems: "center",
    },
    buttonText: { color: "#000", fontWeight: "bold" },
});
