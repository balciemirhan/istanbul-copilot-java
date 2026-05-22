# 🤖 İstanbul Ekonomi AI Co-Pilot (Java Backend) - Karar Destek Ajanı

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)
[![Docker Support](https://img.shields.io/badge/Docker-Hazır-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![Referans Proje](https://img.shields.io/badge/Referans-Python%20v2-blueviolet?style=flat-square)](https://github.com/balciemirhan/istanbul-economy-sentiment-v2)

Bu depo, İstanbul Ekonomi Analizi platformu (**[balciemirhan/istanbul-economy-sentiment-v2](https://github.com/balciemirhan/istanbul-economy-sentiment-v2)**) için özel olarak geliştirilmiş kurumsal düzeyde bir **Yapay Zeka Karar Destek Asistanı** backend servisidir. 

**Spring Boot** mimarisi ve **LangChain4j** yapay zeka entegrasyon kütüphanesi üzerine inşa edilen bu gelişmiş mikroservis; yöneticilerin, belediye planlama ekiplerinin ve veri analistlerinin İstanbul halkının ekonomik nabzını, ulaşım şikayetlerini ve toplumsal duygu durumunu doğal dilde sorgulayarak analiz etmesini sağlar.

> 🛠️ **Geliştiriciler İçin Not:** API detayları, kod mimarisi ve teknik yapılandırma adımları için [Geliştirici Teknik Kılavuzu (README_TECHNICAL.md)](file:///c:/SoftWares/Python/python_project/istanbul-copilot-java/README_TECHNICAL.md) dosyasını inceleyebilirsiniz.

---

## 🎯 Yöneticiler ve Veri Analistleri İçin Neler Sunar?

İstanbul gibi devasa bir metropolde halkın nabzını tutmak ve stratejik kararlar almak zordur. İstanbul Co-Pilot, karmaşık SQL sorguları yazma zorunluluğunu ortadan kaldırarak veritabanınızdaki binlerce tweet verisini doğrudan analiz eder:

*   **Doğal Dil ile Veritabanı Sorgulama (Chat-to-SQL):** *"Ulaşım kategorisinde en yüksek etkileşim alan ilk 3 negatif tweet hangisidir?"* veya *"Halkın en çok öfkelendiği konular nelerdir?"* gibi sorularınızı doğrudan Türkçe olarak sorabilirsiniz. Co-Pilot arka planda güvenli veritabanı sorguları üretir ve yanıtlar.
*   **Anında Görsel Raporlama & Grafik Sentezi (Chart-js):** Yapay zeka, sorduğunuz sorunun doğasına göre (örn. zaman serisi analizi veya kategori dağılımı) dinamik grafik verisi hazırlar. Bu veriler arayüzde anında interaktif grafiklere (Chart.js) dönüşür.
*   **İroni ve Sarkazm Farkındalığı:** Python v2 katmanındaki özel eğitilmiş 128k BERTurk duygu analizi modelinin etiketlediği verileri kullanarak, halkın mecazi ve sarkastik tepkilerini (örn. *"Uçuyoruz maşallah zamlarla"*) doğru bir şekilde süzerek karar destek süreçlerinize aktarır.
*   **Gelişmiş Strateji ve Eylem Planı Önerileri:** Sadece geçmiş veriyi göstermekle kalmaz; yöneticiler için kısa, orta ve uzun vadeli stratejik karar destek planları hazırlar.

---

## 🐳 Docker ile Kolay Kurulum ve Çalıştırma (Konteyner Mimarisi)

Projeyi sistem bağımlılıklarıyla uğraşmadan, izole bir şekilde tek bir komutla ayağa kaldırmak için **Docker** desteği entegre edilmiştir.

### Pratik Çalıştırma Adımları

#### 1. Docker Image (İmaj) Oluşturma
Terminal veya PowerShell üzerinden projenin bulunduğu klasöre gidin ve aşağıdaki komutla Docker imajını oluşturun:
```bash
docker build -t istanbul-copilot-java .
```

#### 2. Konteyneri Başlatma
Konteyneri ayağa kaldırırken, Python sentiment projenizin oluşturduğu SQLite veritabanı dosyasını (`istanbul_ekonomi.db`) konteynere bağlamanız (mount) gerekir. Böylece Java backend servisimiz gerçek verilere doğrudan erişebilir:

```bash
docker run -d \
  -p 8080:8080 \
  -v ./istanbul_ekonomi.db:/istanbul_ekonomi.db \
  -e GEMINI_API_KEY="AIzaSyYourGeminiApiKeyHere" \
  -e SPRING_DATASOURCE_URL="jdbc:sqlite:/istanbul_ekonomi.db" \
  --name istanbul-copilot-service \
  istanbul-copilot-java
```

#### 3. Docker Compose ile Tüm Sistemi Tek Tıkla Ayağa Kaldırma (Önerilen)
Bu Java backend servisi, Python duygu analizi dashboard'u ile birlikte çalışacak şekilde orkestre edilmiştir. Python projesinin ana dizininde bulunan `docker-compose.yml` dosyasını kullanarak tüm sistem bileşenlerini (Python web arayüzü, SQLite veritabanı ve Java Co-Pilot backend servisi) tek bir hamlede çalıştırabilirsiniz:
```bash
docker compose up -d
```

---

## 🔌 API ve Sağlık Durumu Kontrolü (Actuator)

Uygulamanın aktif olup olmadığını ve donanım kaynaklarını izlemek için aşağıdaki endpoint'leri tarayıcınızdan ziyaret edebilirsiniz:

*   **Asistan Sağlık Kontrolü (Health Check):** `http://localhost:8080/actuator/health`
*   **Uygulama Bilgileri (Info):** `http://localhost:8080/actuator/info`

---

## 🔗 Entegrasyon ve Referanslar

*   **Ana Proje (Python v2 Sentiment Tracker):** [balciemirhan/istanbul-economy-sentiment-v2](https://github.com/balciemirhan/istanbul-economy-sentiment-v2)
*   **Özel Eğitilmiş Duygu Analizi Modeli:** [Emirhan41/bert-base-turkish-128k-istanbul-sentiment](https://huggingface.co/Emirhan41/bert-base-turkish-128k-istanbul-sentiment)
