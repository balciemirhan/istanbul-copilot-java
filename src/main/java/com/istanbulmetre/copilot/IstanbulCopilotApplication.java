package com.istanbulmetre.copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import java.util.List;

@SpringBootApplication
public class IstanbulCopilotApplication {

    public static void main(String[] args) {
        configureEnvironment();
        SpringApplication.run(IstanbulCopilotApplication.class, args);
    }

    private static void configureEnvironment() {
        // 1. .env dosyasını bul ve yükle
        List<String> envPaths = List.of(
            "../istanbulmetre_cardiffnlp/.env",
            "../istanbulmetre_cardiffnlp_copilot/.env",
            "../.env",
            ".env"
        );
        File envFile = null;
        for (String path : envPaths) {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                envFile = f;
                break;
            }
        }

        if (envFile != null) {
            System.out.println("🌱 .env dosyası bulundu ve yükleniyor: " + envFile.getAbsolutePath());
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim();
                        String value = line.substring(eqIdx + 1).trim();
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        } else if (value.startsWith("'") && value.endsWith("'")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        System.setProperty(key, value);
                    }
                }
            } catch (Exception e) {
                System.err.println("🚨 .env yüklenirken hata: " + e.getMessage());
            }
        } else {
            System.err.println("⚠️ Uyarı: .env dosyası hiçbir konumda bulunamadı!");
        }

        // 2. SQLite veritabanı dosyasını bul ve SPRING_DATASOURCE_URL olarak ayarla
        List<String> dbPaths = List.of(
            "../istanbulmetre_cardiffnlp/istanbul_ekonomi.db",
            "../istanbulmetre_cardiffnlp_copilot/istanbul_ekonomi.db",
            "../istanbulmetre_cardiffnlp/istanbul_ekonomi_demo.db",
            "../istanbulmetre_cardiffnlp_copilot/istanbul_ekonomi_demo.db",
            "../istanbul_ekonomi.db",
            "istanbul_ekonomi.db"
        );
        File dbFile = null;
        for (String path : dbPaths) {
            File f = new File(path);
            if (f.exists() && f.isFile() && f.length() > 0) {
                dbFile = f;
                break;
            }
        }

        if (dbFile != null) {
            String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath().replace("\\", "/");
            System.setProperty("SPRING_DATASOURCE_URL", dbUrl);
            System.out.println("🗄️ SQLite Veritabanı otomatik tespit edildi: " + dbUrl);
        } else {
            System.err.println("🚨 Hata: Canlı veriye sahip SQLite veritabanı dosyası bulunamadı!");
        }
    }
}

