import React, { useState } from "react";
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    StyleSheet,
    Alert,
    KeyboardAvoidingView,
    Platform,
} from "react-native";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";
import { API_BASE_URL } from "../config"; // 👈 novo import

export default function Login() {
    const router = useRouter();
    const { login } = useAuth();

    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async () => {
        if (!email || !senha) {
            Alert.alert("Atenção", "Preencha e-mail e senha.");
            return;
        }

        try {
            setLoading(true);
            const response = await fetch(`${API_BASE_URL}/clients/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password: senha }),
            });

            if (response.ok) {
                const client = await response.json();
                await login(client);
                Alert.alert("Bem-vindo", `Olá, ${client.name}!`, [
                    { text: "OK", onPress: () => router.replace("/Profile") },
                ]);
            } else if (response.status === 401) {
                const msg = await response.text();
                Alert.alert("Erro de login", msg || "Credenciais inválidas.");
            } else {
                Alert.alert("Erro", "Falha ao realizar login.");
            }
        } catch (err) {
            console.error(err);
            Alert.alert("Erro", "Não foi possível conectar ao servidor.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <KeyboardAvoidingView
            style={styles.container}
            behavior={Platform.OS === "ios" ? "padding" : undefined}
        >
            <View style={styles.inner}>
                <Text style={styles.title}>Bem-vindo de volta!</Text>
                <Text style={styles.subtitle}>Faça login para continuar</Text>

                <TextInput
                    style={styles.input}
                    placeholder="email@dominio.com"
                    placeholderTextColor="#777"
                    autoCapitalize="none"
                    keyboardType="email-address"
                    value={email}
                    onChangeText={setEmail}
                />

                <TextInput
                    style={styles.input}
                    placeholder="Senha"
                    placeholderTextColor="#777"
                    secureTextEntry
                    value={senha}
                    onChangeText={setSenha}
                />

                <TouchableOpacity
                    style={[styles.button, loading && { opacity: 0.6 }]}
                    onPress={handleLogin}
                    disabled={loading}
                >
                    <Text style={styles.buttonText}>
                        {loading ? "Entrando..." : "Entrar"}
                    </Text>
                </TouchableOpacity>

                <TouchableOpacity onPress={() => router.push("/Register")}>
                    <Text style={styles.linkText}>
                        Não tem uma conta? <Text style={styles.linkStrong}>Crie agora</Text>
                    </Text>
                </TouchableOpacity>
            </View>
        </KeyboardAvoidingView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#0E0E10",
        justifyContent: "center",
        paddingHorizontal: 20,
    },
    inner: {
        backgroundColor: "#0E0E10",
        paddingVertical: 40,
    },
    title: {
        color: "#FFF",
        fontSize: 26,
        fontWeight: "bold",
        textAlign: "center",
        marginBottom: 6,
    },
    subtitle: {
        color: "#999",
        fontSize: 15,
        textAlign: "center",
        marginBottom: 30,
    },
    input: {
        backgroundColor: "#1A1A1D",
        borderRadius: 10,
        paddingHorizontal: 16,
        paddingVertical: 12,
        color: "#FFF",
        fontSize: 16,
        marginBottom: 14,
    },
    button: {
        backgroundColor: "#FFF",
        borderRadius: 10,
        paddingVertical: 14,
        alignItems: "center",
        marginTop: 10,
    },
    buttonText: {
        color: "#000",
        fontWeight: "bold",
        fontSize: 16,
    },
    linkText: {
        color: "#AAA",
        textAlign: "center",
        marginTop: 18,
        fontSize: 15,
    },
    linkStrong: {
        color: "#FFF",
        fontWeight: "bold",
    },
});
