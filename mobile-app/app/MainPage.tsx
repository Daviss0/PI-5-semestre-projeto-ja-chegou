import React, { useState, useEffect } from "react";
import { View, StyleSheet, ActivityIndicator, Image } from "react-native";
import MapView, { Marker } from "react-native-maps";

type Truck = {
    id: number;
    plate: string;
    lat: number;
    lng: number;
    routeId: number | null;
};

export default function MainPage() {
    const [trucks, setTrucks] = useState<Truck[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let interval: ReturnType<typeof setInterval>;

        async function loadTrucks() {
            try {
                const response = await fetch("http://192.168.1.107:8080/api/trucks/public/live");
                const data = await response.json();
                setTrucks(data);
            } catch (err) {
                console.error("Erro ao buscar caminhões:", err);
            } finally {
                setLoading(false);
            }
        }

        loadTrucks();
        interval = setInterval(loadTrucks, 1000);

        return () => clearInterval(interval);
    }, []);

    if (loading) {
        return (
            <View style={styles.center}>
                <ActivityIndicator size="large" color="#00aaff" />
            </View>
        );
    }

    return (
        <View style={styles.container}>
            <MapView
                style={styles.map}
                initialRegion={{
                    latitude: -23.55, // ajuste para sua cidade
                    longitude: -46.63,
                    latitudeDelta: 0.1,
                    longitudeDelta: 0.1,
                }}
            >
                {trucks.map((truck) => (
                    <Marker
                        key={truck.id}
                        coordinate={{
                            latitude: truck.lat || 0,
                            longitude: truck.lng || 0,
                        }}
                        title={`Caminhão ${truck.plate}`}
                        description={
                            truck.routeId
                                ? `Rota ID: ${truck.routeId}`
                                : "Sem rota atribuída"
                        }
                    >
                        <Image
                            source={{
                                uri: "https://cdn-icons-png.flaticon.com/512/1995/1995470.png", // ícone de caminhão
                            }}
                            style={{ width: 40, height: 40 }}
                        />
                    </Marker>
                ))}
            </MapView>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    map: {
        flex: 1,
    },
    center: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
    },
});
