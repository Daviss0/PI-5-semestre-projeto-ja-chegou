import React, { useState, useEffect } from "react";
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    StyleSheet,
    ScrollView,
    Alert,
    Platform,
    KeyboardAvoidingView,
} from "react-native";
import DateTimePicker from "@react-native-community/datetimepicker";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";
import { API_BASE_URL } from "../config"; // 👈 referência centralizada ao backend

export default function RegisterDetails() {
    const router = useRouter();

    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");

    const [nome, setNome] = useState("");
    const [cpf, setCpf] = useState("");
    const [celular, setCelular] = useState("");
    const [dataNascimento, setDataNascimento] = useState<Date | null>(null);
    const [showDatePicker, setShowDatePicker] = useState(false);

    const [cep, setCep] = useState("");
    const [logradouro, setLogradouro] = useState("");
    const [bairro, setBairro] = useState("");
    const [cidade, setCidade] = useState("");
    const [uf, setUf] = useState("");
    const [numero, setNumero] = useState("");
    const [complemento, setComplemento] = useState("");

    // Recupera email e senha armazenados na primeira etapa do registro
    useEffect(() => {
        (async () => {
            const stored = await AsyncStorage.getItem("registerData");
            if (stored) {
                const data = JSON.parse(stored);
                setEmail(data.email);
                setSenha(data.senha);
            }
        })();
    }, []);

    const buscarCep = async () => {
        if (cep.length !== 8) {
            Alert.alert("CEP inválido", "Digite um CEP válido com 8 dígitos.");
            return;
        }

        try {
            const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await res.json();

            if (data.erro) {
                Alert.alert("Erro", "CEP não encontrado.");
                return;
            }

            setLogradouro(data.logradouro || "");
            setBairro(data.bairro || "");
            setCidade(data.localidade || "");
            setUf(data.uf || "");
        } catch {
            Alert.alert("Erro", "Falha ao buscar o CEP.");
        }
    };

    const handleRegister = async () => {
        if (!nome || !cpf || !celular || !dataNascimento || !cep || !numero) {
            Alert.alert("Atenção", "Preencha todos os campos obrigatórios.");
            return;
        }

        const usuario = {
            email,
            password: senha,
            name: nome,
            cpf,
            phone: celular,
            birthDate: dataNascimento.toISOString().split("T")[0],
            cep,
            logradouro,
            hood: bairro,
            city: cidade,
            state: uf,
            number: numero,
            complement: complemento,
            role: "CLIENT",
            status: "ATIVO",
        };

        try {
            const response = await fetch(`${API_BASE_URL}/clients/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(usuario),
            });

            if (response.ok) {
                Alert.alert("Sucesso", "Conta criada com sucesso!", [
                    { text: "OK", onPress: () => router.replace("/Login") },
                ]);
            } else {
                const msg = await response.text();
                Alert.alert("Erro", msg || "Não foi possível concluir o cadastro.");
            }
        } catch (err) {
            console.error(err);
            Alert.alert("Erro", "Falha ao conectar com o servidor.");
        }
    };

    return (
        <KeyboardAvoidingView
            style={styles.wrapper}
            behavior={Platform.OS === "ios" ? "padding" : undefined}
        >
            <ScrollView
                contentContainerStyle={styles.container}
                keyboardShouldPersistTaps="handled"
                showsVerticalScrollIndicator={false}
            >
                <Text style={styles.title}>Complete seu cadastro</Text>
                <Text style={styles.subtitle}>Adicione suas informações pessoais</Text>

                <TextInput
                    style={styles.input}
                    placeholder="Nome completo"
                    placeholderTextColor="#777"
                    value={nome}
                    onChangeText={setNome}
                />

                <TextInput
                    style={styles.input}
                    placeholder="CPF (somente números)"
                    placeholderTextColor="#777"
                    keyboardType="numeric"
                    maxLength={11}
                    value={cpf}
                    onChangeText={setCpf}
                />

                <TextInput
                    style={styles.input}
                    placeholder="Celular"
                    placeholderTextColor="#777"
                    keyboardType="phone-pad"
                    value={celular}
                    onChangeText={setCelular}
                />

                <TouchableOpacity
                    style={styles.dateButton}
                    onPress={() => setShowDatePicker(true)}
                >
                    <Text style={styles.dateText}>
                        {dataNascimento
                            ? dataNascimento.toLocaleDateString("pt-BR")
                            : "Data de nascimento"}
                    </Text>
                </TouchableOpacity>

                {showDatePicker && (
                    <DateTimePicker
                        value={dataNascimento || new Date()}
                        mode="date"
                        display={Platform.OS === "ios" ? "spinner" : "default"}
                        onChange={(event, selectedDate) => {
                            setShowDatePicker(false);
                            if (selectedDate) setDataNascimento(selectedDate);
                        }}
                    />
                )}

                {/* CEP e botão de busca */}
                <View style={styles.cepRow}>
                    <TextInput
                        style={[styles.input, { flex: 1 }]}
                        placeholder="CEP"
                        placeholderTextColor="#777"
                        keyboardType="numeric"
                        maxLength={8}
                        value={cep}
                        onChangeText={setCep}
                    />
                    <TouchableOpacity style={styles.buscarButton} onPress={buscarCep}>
                        <Text style={styles.buscarText}>Buscar</Text>
                    </TouchableOpacity>
                </View>

                <TextInput
                    style={styles.input}
                    placeholder="Rua"
                    placeholderTextColor="#777"
                    value={logradouro}
                    editable={false}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Bairro"
                    placeholderTextColor="#777"
                    value={bairro}
                    editable={false}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Cidade"
                    placeholderTextColor="#777"
                    value={cidade}
                    editable={false}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Estado"
                    placeholderTextColor="#777"
                    value={uf}
                    editable={false}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Número"
                    placeholderTextColor="#777"
                    value={numero}
                    onChangeText={setNumero}
                />
                <TextInput
                    style={styles.input}
                    placeholder="Complemento (opcional)"
                    placeholderTextColor="#777"
                    value={complemento}
                    onChangeText={setComplemento}
                />

                <TouchableOpacity style={styles.registerButton} onPress={handleRegister}>
                    <Text style={styles.registerText}>Finalizar Cadastro</Text>
                </TouchableOpacity>
            </ScrollView>
        </KeyboardAvoidingView>
    );
}

const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        backgroundColor: "#0E0E10", // evita fundo branco ao puxar
    },
    container: {
        backgroundColor: "#0E0E10",
        flexGrow: 1,
        paddingHorizontal: 20,
        paddingVertical: 40,
    },
    title: {
        color: "#FFF",
        fontSize: 24,
        fontWeight: "bold",
        marginBottom: 6,
    },
    subtitle: {
        color: "#999",
        fontSize: 15,
        marginBottom: 24,
    },
    input: {
        backgroundColor: "#1A1A1D",
        color: "#FFF",
        borderRadius: 10,
        paddingHorizontal: 16,
        paddingVertical: 12,
        fontSize: 16,
        marginBottom: 12,
    },
    cepRow: {
        flexDirection: "row",
        gap: 10,
        alignItems: "center",
        marginBottom: 12,
    },
    buscarButton: {
        backgroundColor: "#000",
        paddingVertical: 12,
        paddingHorizontal: 20,
        borderRadius: 10,
        alignItems: "center",
        justifyContent: "center",
    },
    buscarText: {
        color: "#FFF",
        fontWeight: "bold",
        fontSize: 15,
    },
    dateButton: {
        backgroundColor: "#1A1A1D",
        borderRadius: 10,
        paddingVertical: 14,
        paddingHorizontal: 16,
        marginBottom: 12,
    },
    dateText: {
        color: "#FFF",
        fontSize: 16,
    },
    registerButton: {
        backgroundColor: "#FFF",
        borderRadius: 10,
        paddingVertical: 14,
        alignItems: "center",
        marginTop: 20,
    },
    registerText: {
        color: "#000",
        fontWeight: "bold",
        fontSize: 16,
    },
});
