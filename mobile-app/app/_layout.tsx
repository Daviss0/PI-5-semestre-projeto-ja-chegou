import React from "react";
import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { AuthProvider, useAuth } from "../context/AuthContext";
import { SelectedRouteProvider } from "../context/SelectedRouteContext";
import { ActivityIndicator, View } from "react-native";

function TabsLayout() {
    const { client, loading } = useAuth();

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
            <Tabs.Screen
                name="MainPage"
                options={{
                    title: "Mapa",
                    tabBarIcon: ({ color, size }) => (
                        <Ionicons name="map-outline" size={size} color={color} />
                    ),
                }}
            />

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
                        if (!loading && !client) {
                            e.preventDefault();
                            navigation.navigate("Login");
                        }
                    },
                })}
            />

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
            <SelectedRouteProvider>
                <TabsLayout />
            </SelectedRouteProvider>
        </AuthProvider>
    );
}
