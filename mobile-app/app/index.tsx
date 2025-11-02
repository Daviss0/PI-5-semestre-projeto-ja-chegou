import { useEffect } from "react";
import { useRouter, useRootNavigationState } from "expo-router";
import { View, ActivityIndicator } from "react-native";

export default function Index() {
    const router = useRouter();
    const navigationState = useRootNavigationState();

    useEffect(() => {
        if (!navigationState?.key) return;

        const timeout = setTimeout(() => {
            router.replace("/MainPage");
        }, 300);

        return () => clearTimeout(timeout);
    }, [navigationState?.key]);

    return (
        <View
            style={{
                flex: 1,
                backgroundColor: "#0E0E10",
                alignItems: "center",
                justifyContent: "center",
            }}
        >
            <ActivityIndicator size="large" color="#fff" />
        </View>
    );
}
