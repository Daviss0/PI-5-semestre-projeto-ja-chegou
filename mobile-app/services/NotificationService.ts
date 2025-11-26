// ==========================================
// NotificationService.ts — FINAL
// ==========================================

import * as Notifications from "expo-notifications";
import { Platform } from "react-native";

// Configuração do comportamento da notificação
Notifications.setNotificationHandler({
    handleNotification: async () => ({
        shouldShowAlert: true,
        shouldPlaySound: true,
        shouldSetBadge: false,
        shouldShowBanner: true,   // necessário em iOS
        shouldShowList: true      // necessário em iOS 15+
    }),
});

class NotificationService {

    static async requestPermissions() {
        const { status } = await Notifications.getPermissionsAsync();
        if (status !== "granted") {
            await Notifications.requestPermissionsAsync();
        }

        // ANDROID → criar canal de notificação
        if (Platform.OS === "android") {
            await Notifications.setNotificationChannelAsync("default", {
                name: "Notificações",
                importance: Notifications.AndroidImportance.HIGH,
            });
        }
    }

    static async sendNotification(title: string, body: string) {
        try {
            await Notifications.scheduleNotificationAsync({
                content: {
                    title,
                    body,
                    sound: true,
                },
                trigger: null, // dispara imediatamente
            });
        } catch (err) {
            console.log("Erro ao enviar notificação:", err);
        }
    }
}

export default NotificationService;
