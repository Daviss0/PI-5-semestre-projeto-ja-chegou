import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();

    const handleLogin = async () => {
        if (!email || !password) {
            Alert.alert("Erro", "Por favor, preencha todos os campos.");
            return;
        }

        // Simulação de login
        if (email === "teste@email.com" && password === "1234") {
            await AsyncStorage.setItem("user", JSON.stringify({ email }));
            router.replace("/MainPage"); // Vai direto pro mapa
        } else {
            Alert.alert("Erro", "Credenciais inválidas. Tente novamente.");
        }
    };

    const handleCreateAccount = () => {
        router.push("/Register");
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Bem-vindo de volta!</Text>
            <Text style={styles.subtitle}>Entre com sua conta para continuar</Text>

            <TextInput
                style={styles.input}
                placeholder="Email"
                placeholderTextColor="#888"
                keyboardType="email-address"
                autoCapitalize="none"
                value={email}
                onChangeText={setEmail}
            />

            <TextInput
                style={styles.input}
                placeholder="Senha"
                placeholderTextColor="#888"
                secureTextEntry
                value={password}
                onChangeText={setPassword}
            />

            <TouchableOpacity style={styles.button} onPress={handleLogin}>
                <Text style={styles.buttonText}>Entrar</Text>
            </TouchableOpacity>

            <Text style={styles.orText}>ou</Text>

            <TouchableOpacity style={styles.googleButton}>
                <Text style={styles.googleText}>Continuar com Google</Text>
            </TouchableOpacity>

            <TouchableOpacity style={styles.appleButton}>
                <Text style={styles.appleText}>Continuar com Apple</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={handleCreateAccount}>
                <Text style={styles.registerText}>
                    Não tem uma conta? <Text style={styles.link}>Criar conta</Text>
                </Text>
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
        paddingHorizontal: 25,
    },
    title: {
        color: "#fff",
        fontSize: 28,
        fontWeight: "700",
        marginBottom: 8,
    },
    subtitle: {
        color: "#ccc",
        fontSize: 15,
        marginBottom: 25,
    },
    input: {
        backgroundColor: "#1A1A1D",
        color: "#fff",
        width: "100%",
        height: 50,
        borderRadius: 10,
        paddingHorizontal: 15,
        marginBottom: 15,
        fontSize: 16,
    },
    button: {
        backgroundColor: "#fff",
        width: "100%",
        height: 50,
        borderRadius: 10,
        alignItems: "center",
        justifyContent: "center",
        marginBottom: 20,
    },
    buttonText: {
        color: "#000",
        fontSize: 16,
        fontWeight: "600",
    },
    orText: {
        color: "#777",
        marginBottom: 20,
    },
    googleButton: {
        backgroundColor: "#1A1A1D",
        width: "100%",
        height: 50,
        borderRadius: 10,
        alignItems: "center",
        justifyContent: "center",
        marginBottom: 10,
    },
    googleText: {
        color: "#fff",
        fontWeight: "600",
    },
    appleButton: {
        backgroundColor: "#1A1A1D",
        width: "100%",
        height: 50,
        borderRadius: 10,
        alignItems: "center",
        justifyContent: "center",
        marginBottom: 20,
    },
    appleText: {
        color: "#fff",
        fontWeight: "600",
    },
    registerText: {
        color: "#888",
        fontSize: 14,
        marginTop: 15,
    },
    link: {
        color: "#fff",
        fontWeight: "600",
    },
});
