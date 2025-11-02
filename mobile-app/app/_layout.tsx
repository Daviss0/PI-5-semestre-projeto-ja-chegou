import React from "react";
import { Tabs, useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function Layout() {
    const router = useRouter();

    // 🔒 Verifica login apenas quando o usuário tenta abrir o Perfil
    const handleProfilePress = async () => {
        const user = await AsyncStorage.getItem("user");
        if (!user) {
            router.push("/Login"); // vai para login se não estiver logado
        } else {
            router.push("/Profile");
        }
    };

    return (
        <Tabs
            screenOptions={{
                headerShown: false,
                tabBarShowLabel: true,
                tabBarActiveTintColor: "#fff",
                tabBarInactiveTintColor: "#777",
                tabBarStyle: {
                    backgroundColor: "#0E0E10",
                    borderTopColor: "#222",
                    height: 70,
                    paddingBottom: 8,
                    paddingTop: 8,
                },
                tabBarLabelStyle: {
                    fontSize: 13,
                    fontWeight: "600",
                },
            }}
        >
            {/* 🌍 Aba do mapa */}
            <Tabs.Screen
                name="MainPage"
                options={{
                    title: "Mapa",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="map-outline" color={color} size={size + 2} />
                    ),
                }}
            />

            {/* 👤 Aba do perfil */}
            <Tabs.Screen
                name="Profile"
                listeners={{
                    tabPress: (e) => {
                        e.preventDefault();
                        handleProfilePress();
                    },
                }}
                options={{
                    title: "Perfil",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="person-circle-outline" color={color} size={size + 2} />
                    ),
                }}
            />

            {/* ❌ Oculta telas extras da barra */}
            <Tabs.Screen name="Login" options={{ href: null }} />
            <Tabs.Screen name="Register" options={{ href: null }} />
            <Tabs.Screen name="index" options={{ href: null }} />
        </Tabs>
    );
}
