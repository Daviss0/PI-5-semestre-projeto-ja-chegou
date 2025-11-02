import React from 'react';
import { View, Text, Button } from 'react-native';

export default function Profile({ navigation }: any) {
    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <Text>Perfil do Usuário</Text>
            <Button title="Sair" onPress={() => navigation.navigate('Login')} />
        </View>
    );
}
