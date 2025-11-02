import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, Linking } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";

export default function Register() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();

    const handleRegister = async () => {
        if (!email || !password) {
            Alert.alert("Erro", "Preencha todos os campos.");
            return;
        }

        // Simulação de cadastro
        await AsyncStorage.setItem("user", JSON.stringify({ email }));
        Alert.alert("Sucesso", "Conta criada com sucesso!");
        router.replace("/MainPage");
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Já Chegou?</Text>
            <Text style={styles.subtitle}>Crie sua conta</Text>
            <Text style={styles.description}>Adicione seu email para cadastro!</Text>

            <TextInput
                style={styles.input}
                placeholder="email@dominio.com"
                placeholderTextColor="#888"
                value={email}
                onChangeText={setEmail}
            />

            <TextInput
                style={styles.input}
                placeholder="Crie uma senha"
                placeholderTextColor="#888"
                secureTextEntry
                value={password}
                onChangeText={setPassword}
            />

            <TouchableOpacity style={styles.button} onPress={handleRegister}>
                <Text style={styles.buttonText}>Continuar</Text>
            </TouchableOpacity>

            <Text style={styles.orText}>ou</Text>

            <TouchableOpacity style={styles.socialButton}>
                <Text style={styles.socialText}>Continuar com Google</Text>
            </TouchableOpacity>

            <TouchableOpacity style={styles.socialButton}>
                <Text style={styles.socialText}>Continuar com Apple</Text>
            </TouchableOpacity>

            <Text style={styles.footerText}>
                Ao continuar, você concorda com os{" "}
                <Text style={styles.link} onPress={() => Linking.openURL("#")}>
                    Termos de Serviço
                </Text>{" "}
                e{" "}
                <Text style={styles.link} onPress={() => Linking.openURL("#")}>
                    Políticas de Privacidade
                </Text>.
            </Text>
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
        fontSize: 30,
        fontWeight: "800",
        marginBottom: 5,
    },
    subtitle: {
        color: "#fff",
        fontSize: 20,
        fontWeight: "600",
        marginBottom: 5,
    },
    description: {
        color: "#aaa",
        fontSize: 14,
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
    socialButton: {
        backgroundColor: "#1A1A1D",
        width: "100%",
        height: 50,
        borderRadius: 10,
        alignItems: "center",
        justifyContent: "center",
        marginBottom: 10,
    },
    socialText: {
        color: "#fff",
        fontWeight: "600",
    },
    footerText: {
        color: "#888",
        fontSize: 13,
        marginTop: 20,
        textAlign: "center",
    },
    link: {
        color: "#3B82F6",
    },
});
