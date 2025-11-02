import React from 'react';
import { View, Text, Button } from 'react-native';

export default function Register({ navigation }: any) {
    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <Text>Página de Cadastro</Text>
            <Button title="Voltar" onPress={() => navigation.goBack()} />
        </View>
    );
}
