import React from "react";
import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { AuthProvider, useAuth } from "../context/AuthContext";
import { SelectedRouteProvider } from "../context/SelectedRouteContext";
import { ActivityIndicator, View } from "react-native";

function TabsLayout() {
    const { client } = useAuth();  // <- sem loading

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
            {/* Mapa */}
            <Tabs.Screen
                name="MainPage"
                options={{
                    title: "Mapa",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="map-outline" size={size} color={color} />
                    ),
                }}
            />

            {/* Perfil */}
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
                        if (!client) {
                            e.preventDefault();
                            navigation.navigate("Login");
                        }
                    },
                })}
            />

            {/* Telas ocultas */}
            <Tabs.Screen name="ProfileData" options={{ href: null }} />
            <Tabs.Screen name="SavedRoutes" options={{ href: null }} />
            <Tabs.Screen name="ProfileNotifications" options={{ href: null }} />

            <Tabs.Screen name="Login" options={{ href: null }} />
            <Tabs.Screen name="Register" options={{ href: null }} />
            <Tabs.Screen name="RegisterDetails" options={{ href: null }} />
            <Tabs.Screen name="index" options={{ href: null }} />

        </Tabs>
    );
}


export default function Layout() {
    return (
        <AuthProvider>
            <SelectedRouteProvider>
                <TabsLayout />
            </SelectedRouteProvider>
        </AuthProvider>
    );
}
