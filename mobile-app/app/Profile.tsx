// === Profile.tsx ===

import React from "react";
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Alert,
    SafeAreaView,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";

export default function Profile() {
    const router = useRouter();
    const { client, logout } = useAuth();

    const handleLogout = async () => {
        await logout();
        router.replace("/Login");
    };

    const handleNavigate = (screen: string) => {
        if (screen === "Meus dados") {
            router.push("/ProfileData");

        } else if (screen === "Minhas rotas") {
            router.push("/SavedRoutes");

        } else if (screen === "Notificações") {
            // 👉 Agora abre a tela de configurações de notificações
            router.push("/ProfileNotifications");

        } else {
            Alert.alert("Em desenvolvimento", `Tela ${screen} será adicionada futuramente.`);
        }
    };

    if (!client) {
        return (
            <View style={styles.loadingContainer}>
                <Text style={styles.loadingText}>Carregando dados...</Text>
            </View>
        );
    }

    return (
        <SafeAreaView style={styles.wrapper}>
            <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 100 }}>
                <View style={styles.header}>
                    <View style={styles.avatarCircle}>
                        <Text style={styles.avatarText}>
                            {client.name ? client.name[0].toUpperCase() : "U"}
                        </Text>
                    </View>
                    <View style={{ flex: 1 }}>
                        <Text style={styles.name}>{client.name}</Text>
                        <Text style={styles.email}>{client.email}</Text>
                    </View>
                </View>

                <View style={styles.menuSection}>
                    <MenuItem icon="person-outline" label="Meus dados" onPress={() => handleNavigate("Meus dados")} />
                    <MenuItem icon="map-outline" label="Minhas rotas" onPress={() => handleNavigate("Minhas rotas")} />

                    {/* AQUI — botão de notificações funcionando */}
                    <MenuItem
                        icon="notifications-outline"
                        label="Notificações"
                        onPress={() => handleNavigate("Notificações")}
                    />
                </View>
            </ScrollView>

            <View style={styles.logoutContainer}>
                <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
                    <Text style={styles.logoutText}>Sair da conta</Text>
                </TouchableOpacity>
            </View>
        </SafeAreaView>
    );
}

const MenuItem = ({ icon, label, onPress }: any) => (
    <TouchableOpacity style={styles.menuItem} onPress={onPress}>
        <View style={styles.menuLeft}>
            <Ionicons name={icon} size={22} color="#fff" />
            <Text style={styles.menuLabel}>{label}</Text>
        </View>
        <Ionicons name="chevron-forward" size={20} color="#777" />
    </TouchableOpacity>
);

const styles = StyleSheet.create({
    wrapper: { flex: 1, backgroundColor: "#0E0E10" },
    container: { flex: 1, paddingHorizontal: 20, paddingTop: 50 },
    loadingContainer: {
        flex: 1,
        backgroundColor: "#0E0E10",
        justifyContent: "center",
        alignItems: "center",
    },
    loadingText: { color: "#888", fontSize: 16 },
    header: { flexDirection: "row", alignItems: "center", marginBottom: 30, gap: 16 },
    avatarCircle: {
        width: 70,
        height: 70,
        borderRadius: 35,
        backgroundColor: "#1A1A1D",
        justifyContent: "center",
        alignItems: "center",
    },
    avatarText: { fontSize: 28, color: "#FFF", fontWeight: "bold" },
    name: { color: "#FFF", fontSize: 22, fontWeight: "bold" },
    email: { color: "#888", fontSize: 14, marginTop: 4 },
    menuSection: {
        backgroundColor: "#1A1A1D",
        borderRadius: 14,
        marginBottom: 25,
        paddingVertical: 4,
    },
    menuItem: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        paddingVertical: 14,
        paddingHorizontal: 10,
        borderBottomWidth: 0.5,
        borderBottomColor: "#333",
    },
    menuLeft: { flexDirection: "row", alignItems: "center", gap: 10 },
    menuLabel: { color: "#FFF", fontSize: 16 },
    logoutContainer: {
        position: "absolute",
        bottom: 20,
        width: "100%",
        alignItems: "center",
    },
    logoutButton: {
        backgroundColor: "#FFF",
        paddingVertical: 14,
        borderRadius: 12,
        alignItems: "center",
        width: "90%",
    },
    logoutText: { color: "#000", fontWeight: "bold", fontSize: 16 },
});
