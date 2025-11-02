import React, { useEffect, useState } from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";

export default function Profile() {
    const router = useRouter();
    const [user, setUser] = useState<any>(null);

    useEffect(() => {
        // simulação de autenticação — depois pode substituir por AsyncStorage ou API
        const storedUser = null; // ainda não logado
        if (!storedUser) {
            router.replace("/Register");
        } else {
            setUser(storedUser);
        }
    }, []);

    if (!user) return null;

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Bem-vindo, {user.name}</Text>
            <TouchableOpacity style={styles.logoutButton}>
                <Text style={styles.logoutText}>Sair</Text>
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#0E0E10",
        alignItems: "center",
        justifyContent: "center",
    },
    title: {
        color: "#FFF",
        fontSize: 20,
        fontWeight: "600",
    },
    logoutButton: {
        marginTop: 20,
        backgroundColor: "#3A7AFE",
        paddingVertical: 10,
        paddingHorizontal: 30,
        borderRadius: 10,
    },
    logoutText: {
        color: "#FFF",
        fontWeight: "bold",
    },
});
