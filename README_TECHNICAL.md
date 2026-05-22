# 🛠️ İstanbul Ekonomi AI Co-Pilot - Technical Reference (Backend Developer Guide)

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-0.34.0-blue.svg?style=flat-square)](https://github.com/langchain4j/langchain4j)
[![Docker Support](https://img.shields.io/badge/Docker-Ready-blue?style=flat-square&logo=docker)](https://www.docker.com/)

This document contains developer-focused architectural documentation, configurations, API patterns, and development guides for the Java Spring Boot + LangChain4j backend microservice.

---

## 🚀 Key Architectural Pillars

*   **Pluggable LLM Integration:** Powered by **LangChain4j** abstractions, making the backend completely decoupled from specific AI providers. Easily switch between cloud models (OpenAI, Gemini) and local offline models (Ollama).
*   **Anti-Hallucination SQL Toolkit:** Features safe, read-only SQLite dialect translations and dynamic schema mapping (`tweet_text` ➔ `text`) to query local sentiment databases with 100% precision.
*   **Zero-Friction Chart Synthesis:** Intercepts visualization requests and formats automated **Chart.js** payloads on-the-fly, reducing user latency to zero.
*   **High-Resiliency Design:** Ready for multi-key rotation and robust failover strategies to maintain 24/7 assistant availability.
*   **Dockerized Deployment:** Zero-friction runtime environment. Packaged inside a multi-stage Docker build, decoupling local system requirements (Java/Maven) from deployment.

---

## ⚙️ Configuration & Environment Variables

Configure the application dynamically by creating or editing the `.env` file in your root workspace (or parent directory):

```env
# --- LLM Provider Selection ---
# Options: gemini | openai | ollama
LLM_PROVIDER=gemini

# --- Model Selection (Optional, defaults apply if blank) ---
# Defaults: gemini-2.5-flash | gpt-4o-mini | llama3
LLM_MODEL_NAME=gemini-2.5-flash

# --- Google Gemini Settings (If LLM_PROVIDER=gemini) ---
# Supports comma-separated keys for automatic rotation and failover
GEMINI_API_KEY=your_gemini_key_1,your_gemini_key_2

# --- OpenAI Settings (If LLM_PROVIDER=openai) ---
# Supports comma-separated keys as well
OPENAI_API_KEY=your_openai_key
# Optional: Set custom base URL for custom proxies, LM Studio, or LocalAI
OPENAI_BASE_URL=

# --- Ollama Settings (If LLM_PROVIDER=ollama) ---
OLLAMA_BASE_URL=http://localhost:11434

# --- Database & Port Settings ---
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:sqlite:../istanbul_ekonomi.db
```

---

## 🐳 Docker Deployment

### 1. Build and Run the Image Locally
To build the Docker image locally from the microservice folder:
```bash
docker build -t istanbul-copilot-java .
```

To run the container locally, mounting the SQLite database from your host directory:
```bash
docker run -d \
  -p 8080:8080 \
  -v ./istanbul_ekonomi.db:/istanbul_ekonomi.db \
  -e GEMINI_API_KEY="your_api_key_here" \
  -e SPRING_DATASOURCE_URL="jdbc:sqlite:/istanbul_ekonomi.db" \
  --name istanbul-copilot-service \
  istanbul-copilot-java
```

### 2. Run via Docker Compose (Recommended)
This service is orchestrated together with the Python sentiment dashboard. In the parent project directory, simply execute:
```bash
docker compose up -d
```

---

## 🛠️ Local Development (Without Docker)

If you have a local Java 21 JDK and Maven installed:

1.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
2.  Or use the supplied custom wrapper script on Windows:
    ```cmd
    .\mvnw_local.cmd spring-boot:run
    ```

---

## 🏁 CI/CD Pipeline (GitHub Actions)

This repository includes a pre-configured CI/CD workflow (`.github/workflows/docker-publish.yml`).

On every push to the `main` branch:
1.  Code is checked out and compiled.
2.  The workflow logs into the **GitHub Container Registry (GHCR)**.
3.  Builds the multi-stage Docker image, tagging it as `latest` and `sha-<commit-hash>`.
4.  Pushes the image to: `ghcr.io/emirhanbalci/istanbul-copilot-java:latest`.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
