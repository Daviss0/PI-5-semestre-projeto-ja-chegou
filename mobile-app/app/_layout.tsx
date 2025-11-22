// mobile-app/app/_layout.tsx
import React from "react";
import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { AuthProvider, useAuth } from "../context/AuthContext";
import { ActivityIndicator, View } from "react-native";

function TabsLayout() {
    const { client, loading } = useAuth();

    // Enquanto estiver carregando o estado do usuário, não renderiza as tabs
    if (loading) {
        return (
            <View
                style={{
                    flex: 1,
                    backgroundColor: "#0E0E10",
                    justifyContent: "center",
                    alignItems: "center",
                }}
            >
                <ActivityIndicator size="large" color="#fff" />
            </View>
        );
    }

    return (
        <Tabs
            screenOptions={{
                tabBarStyle: {
                    backgroundColor: "#0E0E10",
                    borderTopColor: "#222",
                    height: 65,
                },
                tabBarActiveTintColor: "#fff",
                tabBarInactiveTintColor: "#777",
                headerShown: false,
            }}
        >
            {/* Tela Principal (Mapa) */}
            <Tabs.Screen
                name="MainPage"
                options={{
                    title: "Mapa",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="map-outline" size={size} color={color} />
                    ),
                }}
            />

            {/* Perfil (só redireciona se REALMENTE não estiver logado) */}
            <Tabs.Screen
                name="Profile"
                options={{
                    title: "Perfil",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="person-outline" size={size} color={color} />
                    ),
                }}
                listeners={({ navigation }) => ({
                    tabPress: (e) => {
                        // 🚀 AGORA FUNCIONA DO JEITO IDEAL:
                        // Só redireciona se: loading terminou E client for null
                        if (!loading && !client) {
                            e.preventDefault();
                            navigation.navigate("Login");
                        }
                    },
                })}
            />

            {/* Telas ocultas (não aparecem na TabBar) */}
            <Tabs.Screen name="Login" options={{ href: null, headerShown: false }} />
            <Tabs.Screen name="Register" options={{ href: null, headerShown: false }} />
            <Tabs.Screen name="RegisterDetails" options={{ href: null, headerShown: false }} />
            <Tabs.Screen name="index" options={{ href: null, headerShown: false }} />
        </Tabs>
    );
}

export default function Layout() {
    return (
        <AuthProvider>
            <TabsLayout />
        </AuthProvider>
    );
}
