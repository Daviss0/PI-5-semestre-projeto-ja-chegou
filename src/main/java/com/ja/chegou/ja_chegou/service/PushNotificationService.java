package com.ja.chegou.ja_chegou.service;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.Security;

@Service
public class PushNotificationService {

   @Value("${vapid.public.key}")
    private String publicKey;
   @Value("${vapid.private.key}")
    private String privateKey;
   @Value("${vapid.subject}")
    private String subject;

    public void sendNotification(String endpoint, String p256dh, String auth, String payload)
            throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        Notification notification = new Notification(endpoint, p256dh, auth, payload);

        PushService pushService = new PushService();
        pushService.setPublicKey(Utils.loadPublicKey(publicKey));
        pushService.setPrivateKey(Utils.loadPrivateKey(privateKey));
        pushService.setSubject(subject);

        pushService.send(notification);

        System.out.println("Notificação enviada com sucesso!");
    }
}
