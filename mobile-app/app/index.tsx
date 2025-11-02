import { View, Text, Button, StyleSheet } from "react-native";
import { useRouter } from "expo-router";

export default function Index() {
    const router = useRouter();

    return (
        <View style={styles.container}>
            <Text style={styles.text}>🚛 Bem-vindo ao JÁ CHEGOU</Text>
            <Button title="Entrar" onPress={() => router.push("/MainPage")} />
            <Button title="Cadastrar" onPress={() => router.push("/Register")} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, justifyContent: "center", alignItems: "center" },
    text: { fontSize: 18, fontWeight: "bold", marginBottom: 20 },
});
