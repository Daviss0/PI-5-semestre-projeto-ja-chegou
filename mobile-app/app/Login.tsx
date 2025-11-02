import React from 'react';
import { View, Text, Button } from 'react-native';

export default function Login({ navigation }: any) {
    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
    <Text>Página de Login</Text>
    <Button title="Entrar" onPress={() => navigation.navigate('MainPage')} />
    <Button title="Cadastrar" onPress={() => navigation.navigate('Register')} />
    </View>
);
}
