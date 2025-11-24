// mobile-app/app/ProfileData.tsx
import React from "react";
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";

export default function ProfileData() {
    const router = useRouter();
    const { client } = useAuth();

    if (!client) {
        return (
            <View style={styles.loadingContainer}>
                <Text style={styles.loadingText}>Carregando dados...</Text>
            </View>
        );
    }

    return (
        <ScrollView style={styles.container}>
            <TouchableOpacity style={styles.backButton} onPress={() => router.replace("/Profile")}>

            <Ionicons name="arrow-back" size={24} color="#fff" />
            </TouchableOpacity>

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

            {/* Informações pessoais */}
            <View style={styles.infoCard}>
                <Text style={styles.sectionTitle}>Informações pessoais</Text>
                <InfoRow label="Nome" value={client.name} />
                <InfoRow label="CPF" value={client.cpf} />
                <InfoRow label="Celular" value={client.phone} />
                <InfoRow label="Nascimento" value={client.birthDate} />
            </View>

            {/* Endereço */}
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
    backButton: { marginBottom: 20 },
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
    avatarText: { fontSize: 28, color: "#FFF", fontWeight: "bold" },
    name: { color: "#FFF", fontSize: 22, fontWeight: "bold" },
    email: { color: "#888", fontSize: 14, marginTop: 4 },

    infoCard: {
        backgroundColor: "#1A1A1D",
        borderRadius: 14,
        padding: 16,
        marginBottom: 20,
    },
    sectionTitle: { color: "#FFF", fontSize: 16, fontWeight: "bold", marginBottom: 10 },
    infoRow: {
        flexDirection: "row",
        justifyContent: "space-between",
        borderBottomWidth: 0.5,
        borderBottomColor: "#333",
        paddingVertical: 8,
    },
    label: { color: "#AAA", fontSize: 15 },
    value: {
        color: "#FFF",
        fontSize: 15,
        fontWeight: "500",
        maxWidth: "60%",
        textAlign: "right",
    },
});
