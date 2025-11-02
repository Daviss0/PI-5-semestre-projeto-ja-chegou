import React, { useEffect, useState } from "react";
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Image,
    Alert,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";

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

    if (!client) {
        return (
            <View style={styles.loadingContainer}>
                <Text style={styles.loadingText}>Carregando dados...</Text>
            </View>
        );
    }

    return (
        <ScrollView
            style={styles.container}
            contentContainerStyle={{ paddingBottom: 40 }}
            showsVerticalScrollIndicator={false}
        >
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

            <View style={styles.infoCard}>
                <Text style={styles.sectionTitle}>Informações pessoais</Text>

                <InfoRow label="Nome" value={client.name} />
                <InfoRow label="CPF" value={client.cpf} />
                <InfoRow label="Celular" value={client.phone} />
                <InfoRow label="Nascimento" value={client.birthDate} />
            </View>

            <View style={styles.infoCard}>
                <Text style={styles.sectionTitle}>Endereço</Text>

                <InfoRow label="CEP" value={client.cep} />
                <InfoRow label="Rua" value={client.logradouro} />
                <InfoRow label="Número" value={client.number} />
                <InfoRow label="Bairro" value={client.hood} />
                <InfoRow label="Cidade" value={client.city} />
                <InfoRow label="Estado" value={client.state} />
                {client.complement && (
                    <InfoRow label="Complemento" value={client.complement} />
                )}
            </View>

            <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
                <Text style={styles.logoutText}>Sair da conta</Text>
            </TouchableOpacity>
        </ScrollView>
    );
}

const InfoRow = ({ label, value }: { label: string; value: string }) => (
    <View style={styles.infoRow}>
        <Text style={styles.label}>{label}</Text>
        <Text style={styles.value}>{value || "-"}</Text>
    </View>
);

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#0E0E10",
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
    infoCard: {
        backgroundColor: "#1A1A1D",
        borderRadius: 14,
        padding: 16,
        marginBottom: 20,
    },
    sectionTitle: {
        color: "#FFF",
        fontSize: 16,
        fontWeight: "bold",
        marginBottom: 10,
    },
    infoRow: {
        flexDirection: "row",
        justifyContent: "space-between",
        borderBottomWidth: 0.5,
        borderBottomColor: "#333",
        paddingVertical: 8,
    },
    label: {
        color: "#AAA",
        fontSize: 15,
    },
    value: {
        color: "#FFF",
        fontSize: 15,
        fontWeight: "500",
        maxWidth: "60%",
        textAlign: "right",
    },
    logoutButton: {
        backgroundColor: "#FFF",
        paddingVertical: 14,
        borderRadius: 12,
        alignItems: "center",
    },
    logoutText: {
        color: "#000",
        fontWeight: "bold",
        fontSize: 16,
    },
});
