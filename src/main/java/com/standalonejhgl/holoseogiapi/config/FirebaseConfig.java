package com.standalonejhgl.holoseogiapi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {

            InputStream serviceAccount;

            // 도커 볼륨(/app)에서 우선 탐색
            File externalFile = new File("/app/firebase-service-account.json");
            if (externalFile.exists()) {
                log.info("Using external Firebase key at: {}", externalFile.getAbsolutePath());
                serviceAccount = new FileInputStream(externalFile);
            } else {
                // 로컬 개발 환경(classpath) fallback
                log.info("Using classpath Firebase key (local dev)");
                serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
            }
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                    .createScoped(List.of(
                            "https://www.googleapis.com/auth/firebase.messaging",
                            "https://www.googleapis.com/auth/cloud-platform"
                    ));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .setProjectId("standalone-ff75e")
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            System.out.println("FirebaseApp 초기화 완료");
            log.info("🔥 Firebase initialized with project: {}", FirebaseApp.getInstance().getOptions().getProjectId());
            return app;
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        System.out.println("✅ FirebaseMessaging Bean 등록 완료");
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
