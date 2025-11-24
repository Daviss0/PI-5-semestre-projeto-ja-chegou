import React, { useState } from "react";
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    StyleSheet,
    Alert,
} from "react-native";
import { useRouter } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function Register() {
    const router = useRouter();
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");

    const handleContinue = async () => {
        if (!email.includes("@") || senha.length < 6) {
            Alert.alert(
                "Erro",
                "Digite um e-mail válido e uma senha com pelo menos 6 caracteres."
            );
            return;
        }

        try {
            // Salva email e senha localmente para usar depois na tela RegisterDetails
            await AsyncStorage.setItem(
                "registerData",
                JSON.stringify({ email, senha })
            );

            router.push("/RegisterDetails");
        } catch (err) {
            console.error(err);
            Alert.alert("Erro", "Não foi possível prosseguir com o cadastro.");
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Já Chegou?</Text>
            <Text style={styles.subtitle}>Crie sua conta</Text>

            <TextInput
                style={styles.input}
                placeholder="email@dominio.com"
                placeholderTextColor="#888"
                value={email}
                onChangeText={setEmail}
                autoCapitalize="none"
                keyboardType="email-address"
            />

            <TextInput
                style={styles.input}
                placeholder="Senha"
                placeholderTextColor="#888"
                secureTextEntry
                value={senha}
                onChangeText={setSenha}
            />

            <TouchableOpacity style={styles.button} onPress={handleContinue}>
                <Text style={styles.buttonText}>Continuar</Text>
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#0E0E10",
        justifyContent: "center",
        padding: 20,
    },
    title: {
        color: "#fff",
        fontSize: 28,
        fontWeight: "bold",
        textAlign: "center",
    },
    subtitle: {
        color: "#aaa",
        fontSize: 16,
        textAlign: "center",
        marginBottom: 30,
    },
    input: {
        backgroundColor: "#1A1A1D",
        borderRadius: 10,
        paddingHorizontal: 16,
        paddingVertical: 12,
        color: "#fff",
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
});
