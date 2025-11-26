// ==========================================
// ProfileNotifications.tsx — FINAL (com backend + slider novo)
// ==========================================

import React, { useEffect, useState } from "react";
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    Alert,
    ScrollView,
} from "react-native";
import Slider from "@react-native-assets/slider";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";
import { API_BASE_URL } from "../config";

export default function ProfileNotifications() {

    const router = useRouter();
    const { client } = useAuth();

    const [enabled, setEnabled] = useState(false);
    const [notifyMinutes, setNotifyMinutes] = useState(3);
    const [notifyDistance, setNotifyDistance] = useState(600);
    const [loading, setLoading] = useState(true);

    // ============================================================
    // 🔄 CARREGAR CONFIGURAÇÕES DO BACKEND
    // ============================================================
    useEffect(() => {
        if (!client) return;

        (async () => {
            try {
                const res = await fetch(`${API_BASE_URL}/user/notifications/${client.email}`);
                if (res.ok) {
                    const data = await res.json();

                    setEnabled(data.enabled ?? false);
                    setNotifyMinutes(data.notifyMinutes ?? 3);
                    setNotifyDistance(data.notifyDistance ?? 600);
                }
            } catch (err) {
                console.log("Erro carregando notificações:", err);
            }
            setLoading(false);
        })();
    }, [client]);

    // ============================================================
    // 💾 SALVAR CONFIGURAÇÕES NO BACKEND
    // ============================================================
    const handleSave = async () => {
        if (!client) return;

        try {
            await fetch(`${API_BASE_URL}/user/notifications/save`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: client.email,
                    enabled,
                    notifyMinutes,
                    notifyDistance,
                }),
            });

            Alert.alert("Sucesso", "Preferências salvas!");
        } catch (err) {
            Alert.alert("Erro", "Falha ao salvar configurações.");
        }
    };

    if (loading) {
        return (
            <View style={styles.loadingContainer}>
                <Text style={{ color: "#FFF" }}>Carregando...</Text>
            </View>
        );
    }

    // ============================================================
    // UI
    // ============================================================
    return (
        <ScrollView style={styles.wrapper} contentContainerStyle={{ paddingBottom: 60 }}>

            {/* Botão voltar */}
            <TouchableOpacity style={styles.backButton} onPress={() => router.push("/Profile")}>
                <Ionicons name="arrow-back" size={28} color="#FFF" />
            </TouchableOpacity>

            <Text style={styles.title}>Notificações</Text>

            {/* Ativar notificações */}
            <View style={styles.row}>
                <Text style={styles.label}>Ativar notificações</Text>

                <TouchableOpacity
                    onPress={() => setEnabled(!enabled)}
                    style={[styles.switch, enabled && styles.switchOn]}
                >
                    <View style={[styles.switchThumb, enabled && styles.switchThumbOn]} />
                </TouchableOpacity>
            </View>

            {/* Tempo */}
            <View style={styles.block}>
                <Text style={styles.blockLabel}>Tempo antes da chegada</Text>
                <Text style={styles.blockValue}>{notifyMinutes} minutos</Text>

                <Slider
                    value={notifyMinutes}
                    minimumValue={1}
                    maximumValue={20}
                    step={1}
                    minimumTrackTintColor="#FFF"
                    maximumTrackTintColor="#333"
                    thumbTintColor="#FFF"
                    onValueChange={(v) => setNotifyMinutes(v)}
                />
            </View>

            {/* Distância */}
            <View style={styles.block}>
                <Text style={styles.blockLabel}>Distância mínima</Text>
                <Text style={styles.blockValue}>{notifyDistance} metros</Text>

                <Slider
                    value={notifyDistance}
                    minimumValue={200}
                    maximumValue={2000}
                    step={100}
                    minimumTrackTintColor="#FFF"
                    maximumTrackTintColor="#333"
                    thumbTintColor="#FFF"
                    onValueChange={(v) => setNotifyDistance(v)}
                />
            </View>

            {/* Botão salvar */}
            <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
                <Text style={styles.saveText}>Salvar</Text>
            </TouchableOpacity>

        </ScrollView>
    );
}

// ==========================================
// ESTILOS
// ==========================================

const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        backgroundColor: "#0E0E10",
    },

    loadingContainer: {
        flex: 1,
        backgroundColor: "#0E0E10",
        justifyContent: "center",
        alignItems: "center",
    },

    backButton: {
        position: "absolute",
        top: 55,
        left: 20,
        zIndex: 20,
        padding: 6,
    },

    title: {
        color: "#FFF",
        fontSize: 26,
        fontWeight: "bold",
        textAlign: "center",
        marginTop: 110,
        marginBottom: 20,
    },

    row: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginHorizontal: 20,
        marginBottom: 20,
    },

    label: {
        color: "#DDD",
        fontSize: 16,
    },

    switch: {
        width: 52,
        height: 30,
        borderRadius: 15,
        backgroundColor: "#333",
        justifyContent: "center",
        padding: 3,
    },
    switchOn: {
        backgroundColor: "#FFF",
    },
    switchThumb: {
        width: 24,
        height: 24,
        borderRadius: 12,
        backgroundColor: "#FFF",
        transform: [{ translateX: 0 }],
    },
    switchThumbOn: {
        backgroundColor: "#000",
        transform: [{ translateX: 22 }],
    },

    block: {
        backgroundColor: "#1A1A1D",
        marginHorizontal: 20,
        padding: 18,
        borderRadius: 14,
        marginBottom: 25,
        borderColor: "#333",
        borderWidth: 1,
    },

    blockLabel: {
        color: "#DDD",
        fontSize: 16,
        marginBottom: 10,
    },

    blockValue: {
        color: "#FFF",
        fontSize: 22,
        fontWeight: "bold",
        marginBottom: 10,
    },

    saveButton: {
        backgroundColor: "#FFF",
        marginHorizontal: 20,
        paddingVertical: 14,
        borderRadius: 12,
        alignItems: "center",
        marginTop: 10,
        marginBottom: 60,
    },
    saveText: {
        color: "#000",
        fontSize: 18,
        fontWeight: "bold",
    },
});
