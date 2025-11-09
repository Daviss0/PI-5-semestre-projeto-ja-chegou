// mobile-app/app/Profile.tsx
import React, { useEffect, useState } from "react";
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Alert,
    SafeAreaView,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";

export default function Profile() {
    const router = useRouter();
    const [client, setClient] = useState<any>(null);

    useEffect(() => {
        (async () => {
            try {
                const storedClient = await AsyncStorage.getItem("clientData");
                if (storedClient) {
                    setClient(JSON.parse(storedClient));
                } else {
                    Alert.alert("Sessão expirada", "Por favor, faça login novamente.");
                    router.replace("/Login");
                }
            } catch (err) {
                console.error("Erro ao carregar dados:", err);
            }
        })();
    }, []);

    const handleLogout = async () => {
        await AsyncStorage.removeItem("clientData");
        router.replace("/Login");
    };

    const handleNavigate = (screen: string) => {
        if (screen === "Meus dados") router.push("/ProfileData");
        else Alert.alert("Em desenvolvimento", `Tela ${screen} será adicionada futuramente.`);
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
            <ScrollView
                style={styles.container}
                contentContainerStyle={{ paddingBottom: 100 }}
                showsVerticalScrollIndicator={false}
            >
                {/* 🔹 Cabeçalho */}
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

                {/* 🔸 Menu */}
                <View style={styles.menuSection}>
                    <MenuItem
                        icon="person-outline"
                        label="Meus dados"
                        onPress={() => handleNavigate("Meus dados")}
                    />
                    <MenuItem
                        icon="map-outline"
                        label="Minhas rotas"
                        onPress={() => handleNavigate("Minhas rotas")}
                    />
                    <MenuItem
                        icon="notifications-outline"
                        label="Notificações"
                        onPress={() => handleNavigate("Notificações")}
                    />
                </View>
            </ScrollView>

            {/* 🔻 Botão fixo inferior */}
            <View style={styles.logoutContainer}>
                <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
                    <Text style={styles.logoutText}>Sair da conta</Text>
                </TouchableOpacity>
            </View>
        </SafeAreaView>
    );
}

const MenuItem = ({
                      icon,
                      label,
                      onPress,
                  }: {
    icon: keyof typeof Ionicons.glyphMap;
    label: string;
    onPress: () => void;
}) => (
    <TouchableOpacity style={styles.menuItem} onPress={onPress}>
        <View style={styles.menuLeft}>
            <Ionicons name={icon} size={22} color="#fff" />
            <Text style={styles.menuLabel}>{label}</Text>
        </View>
        <Ionicons name="chevron-forward" size={20} color="#777" />
    </TouchableOpacity>
);

const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        backgroundColor: "#0E0E10",
    },
    container: {
        flex: 1,
        paddingHorizontal: 20,
        paddingTop: 50,
    },
    loadingContainer: {
        flex: 1,
        backgroundColor: "#0E0E10",
        justifyContent: "center",
        alignItems: "center",
    },
    loadingText: { color: "#888", fontSize: 16 },
    header: {
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 30,
        gap: 16,
    },
    avatarCircle: {
        width: 70,
        height: 70,
        borderRadius: 35,
        backgroundColor: "#1A1A1D",
        justifyContent: "center",
        alignItems: "center",
    },
    avatarText: {
        fontSize: 28,
        color: "#FFF",
        fontWeight: "bold",
    },
    name: {
        color: "#FFF",
        fontSize: 22,
        fontWeight: "bold",
    },
    email: {
        color: "#888",
        fontSize: 14,
        marginTop: 4,
    },
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
    logoutText: {
        color: "#000",
        fontWeight: "bold",
        fontSize: 16,
    },
});
