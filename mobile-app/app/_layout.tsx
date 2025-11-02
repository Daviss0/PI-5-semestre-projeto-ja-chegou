// mobile-app/app/_layout.tsx
import React from "react";
import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { AuthProvider, useAuth } from "../context/AuthContext";
import { ActivityIndicator, View } from "react-native";

function TabsLayout() {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <View style={{ flex: 1, backgroundColor: "#0E0E10", justifyContent: "center", alignItems: "center" }}>
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
            {/* 🔹 Mapa (sempre visível) */}
            <Tabs.Screen
                name="MainPage"
                options={{
                    title: "Mapa",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="map-outline" size={size} color={color} />
                    ),
                }}
            />

            {/* 🔹 Perfil (leva ao Login se deslogado) */}
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
                        if (!user) {
                            e.preventDefault();
                            navigation.navigate("Login");
                        }
                    },
                })}
            />

            {/* Telas ocultas (fora da barra) */}
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
