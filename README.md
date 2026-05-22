# 🤖 İstanbul Ekonomi AI Co-Pilot (Java Backend) - Karar Destek Ajanı

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)
[![Docker Support](https://img.shields.io/badge/Docker-Hazır-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![Referans Proje](https://img.shields.io/badge/Referans-Python%20v2-blueviolet?style=flat-square)](https://github.com/balciemirhan/istanbul-economy-sentiment-v2)

Bu depo, İstanbul Ekonomi Analizi platformu (**[balciemirhan/istanbul-economy-sentiment-v2](https://github.com/balciemirhan/istanbul-economy-sentiment-v2)**) için özel olarak geliştirilmiş kurumsal düzeyde bir **Yapay Zeka Karar Destek Asistanı** backend servisidir.

**Spring Boot** mimarisi ve **LangChain4j** yapay zeka entegrasyon kütüphanesi üzerine inşa edilen bu gelişmiş mikroservis; yöneticilerin, belediye planlama ekiplerinin ve veri analistlerinin İstanbul halkının ekonomik nabzını, ulaşım şikayetlerini ve toplumsal duygu durumunu doğal dilde sorgulayarak analiz etmesini sağlar.

> 🛠️ **Geliştiriciler İçin Not:** API detayları, kod mimarisi ve teknik yapılandırma adımları için [Geliştirici Teknik Kılavuzu (README_TECHNICAL.md)](README_TECHNICAL.md) dosyasını inceleyebilirsiniz.

---

## ▶️ Demo Videosu

Aşağıdaki videoda Co-Pilot asistanının canlı kullanımı; Türkçe doğal dil sorgulaması, otomatik grafik üretimi ve sesli asistan özellikleri kısa bir tur ile gösterilmektedir:

<div align="center">

<table>
  <tr>
    <th align="center">🎬 İstanbul Co-Pilot — Canlı Demo</th>
  </tr>
  <tr>
    <td align="center">

https://github.com/user-attachments/assets/8366c155-1480-4ef4-8132-7d8c7627d567

  </td>
  </tr>
  <tr>
    <td align="center"><em>Doğal dil sorgusu · Otomatik grafik · Sesli asistan · Eylem planı üretimi</em></td>
  </tr>
</table>

</div>


---

## 🖼️ Ekran Görüntüleri (Co-Pilot Arayüzü Önizleme)

Aşağıda Co-Pilot asistanının farklı kullanım senaryolarından alınan ekran görüntüleri yer almaktadır:

<div align="center">

<table>
  <tr>
    <th align="center">🤖 Co-Pilot Ana Arayüzü</th>
    <th align="center">📊 Otomatik Grafik Üretimi</th>
  </tr>
  <tr>
    <td align="center"><img alt="Co-Pilot Ana Arayüzü" src="https://github.com/user-attachments/assets/75709b58-3430-468d-9a53-0e81fce21f17" width="460" /></td>
    <td align="center"><img alt="Otomatik Grafik Üretimi" src="https://github.com/user-attachments/assets/69a94f2e-8b1b-48a8-9c24-415c2420e63c" width="460" /></td>
  </tr>
  <tr>
    <td align="center"><em>Türkçe doğal dil sorusu → anında AI yanıtı</em></td>
    <td align="center"><em>Sorgusuna göre otomatik Chart.js grafik sentezi</em></td>
  </tr>
  <tr>
    <th align="center">🎙️ Spacebar Bas-Konuş Sesli Sorgulama</th>
    <th align="center">🔍 İlk 10 Tweet — SQL'siz Doğal Dil Sorgusu</th>
  </tr>
  <tr>
    <td align="center"><img alt="Spacebar Bas-Konuş Sesli Sorgulama" src="https://github.com/user-attachments/assets/502e86dc-9e44-421a-9a0d-0192f57f2d2a" width="460" /></td>
    <td align="center"><img alt="İlk 10 Tweet SQL'siz Sorgu" src="https://github.com/user-attachments/assets/4fc04991-c345-430f-a870-8fd496d3882e" width="460" /></td>
  </tr>
  <tr>
    <td align="center"><em>Klavyeye basmadan sesli sorgulama yapabilme</em></td>
    <td align="center"><em>SQL yazmadan istediğin tweet kayıtlarını listele</em></td>
  </tr>
  <tr>
    <th align="center">💬 Sohbet İçi Derin Analiz & Özet Yanıtı</th>
    <th align="center">🧠 3 Aşamalı Stratejik Karar Destek & Eylem Planı</th>
  </tr>
  <tr>
    <td align="center"><img alt="Sohbet İçi Derin Analiz" src="https://github.com/user-attachments/assets/e0a7e6ea-caf4-4acd-8047-913aafeb4621" width="460" /></td>
    <td align="center"><img alt="3 Aşamalı Stratejik Karar Destek ve Eylem Planı" src="https://github.com/user-attachments/assets/f7474f1a-64ec-404a-acbf-51d465ecefb8" width="460" /></td>
  </tr>
  <tr>
    <td align="center"><em>Özet, yorum ve analiz taleplerine derinlemesine yanıt</em></td>
    <td align="center"><em>Kısa / orta / uzun vadeli stratejik öneri üretimi</em></td>
  </tr>
</table>

</div>

---

## 🎯 Yöneticiler ve Veri Analistleri İçin Neler Sunar?

İstanbul gibi devasa bir metropolde halkın nabzını tutmak ve stratejik kararlar almak zordur. İstanbul Co-Pilot, karmaşık SQL sorguları yazma zorunluluğunu ortadan kaldırarak veritabanınızdaki binlerce tweet verisini doğrudan analiz eder:

*   **Doğal Dil ile Veritabanı Sorgulama (Chat-to-SQL):** *"Ulaşım kategorisinde en yüksek etkileşim alan ilk 3 negatif tweet hangisidir?"* veya *"Halkın en çok öfkelendiği konular nelerdir?"* gibi sorularınızı doğrudan Türkçe olarak sorabilirsiniz. Co-Pilot arka planda güvenli veritabanı sorguları üretir ve yanıtlar.
*   **Anında Görsel Raporlama & Grafik Sentezi (Chart.js):** Yapay zeka, sorduğunuz sorunun doğasına göre (örn. zaman serisi analizi veya kategori dağılımı) dinamik grafik verisi hazırlar. Bu veriler arayüzde anında interaktif grafiklere dönüşür.
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
