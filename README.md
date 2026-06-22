# ServiConecta Mobile 🛠️🧡
**Proyecto Final - Programación de Dispositivos Móviles**

ServiConecta Mobile es una plataforma Full-Stack nativa para Android diseñada para conectar a usuarios con profesionales técnicos en El Salvador. El proyecto representa la **refactorización integral** de una solución web previa, migrando toda la infraestructura a un ecosistema basado 100% en **Kotlin**.

---

## ✨ Funcionalidades Principales (MVP)

*   **Navegación de Invitado:** Exploración libre de categorías y perfiles profesionales sin registro obligatorio.
*   **Gestión de Perfiles:** Los contratistas pueden configurar su biografía, años de experiencia y categoría de servicio.
*   **Chat en Tiempo Real:** Comunicación bidireccional instantánea mediante **Firebase Firestore**.
*   **Sistema de Reputación:** Calificaciones y reseñas dinámicas calculadas en tiempo real desde el servidor.
*   **Autenticación Segura:** Registro y Login con roles diferenciados y seguridad mediante hashing de contraseñas.

---

## 🎨 Identidad Visual
La aplicación implementa una estética de alto contraste en **Negro y Naranja**, optimizada para visualización moderna y un enfoque de alta eficiencia en la interfaz de usuario (Material Design 3).

> **Nota:** Se recomienda adjuntar capturas de pantalla en la carpeta `/assets` para visualizarlas aquí.

---

## 🛠️ Stack Tecnológico

| Componente | Tecnologías |
| :--- | :--- |
| **Lenguaje** | Kotlin |
| **Frontend** | Jetpack Compose, Material 3, ViewModel, LiveData |
| **Networking** | Retrofit 2, Gson, OkHttp |
| **Backend** | Ktor Framework (Kotlin Server), Netty Engine |
| **Base de Datos** | Microsoft SQL Server (Relacional), Firebase Firestore (NoSQL) |
| **Seguridad** | BCrypt para protección de credenciales |

---

## 🏗️ Arquitectura
El sistema sigue el patrón **MVVM (Model-View-ViewModel)** y el **Patrón Repositorio**, garantizando una separación clara entre la interfaz de usuario y la lógica de negocio. Esto permite que el cliente móvil sea independiente de la implementación específica del backend.

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
* Android Studio (Versión Ladybug o superior)
* IntelliJ IDEA (para el Backend)
* Instancia de SQL Server con la base de datos `ServiConectaDB`.
* Archivo `google-services.json` de Firebase (debe colocarse en `ServiConectaMobile/app/`).

### Configuración del Backend
1. Abrir la carpeta `ServiConectaMobileBackend` en IntelliJ.
2. Configurar la cadena de conexión en `src/main/kotlin/database/DbConnection.kt`.
3. Ejecutar la función `main` (Puerto por defecto: 8080).

### Configuración de la App Android
1. Abrir la carpeta `ServiConectaMobile` en Android Studio.
2. Actualizar la IP de tu PC en `network/ApiService.kt` (reemplazar por tu IPv4 local).
3. Sincronizar Gradle y ejecutar en un dispositivo físico o emulador.

---

## 👥 Equipo de Desarrollo
*   **Mario Alexander Molina Fuentes** – 00372624
*   **Miguel Alberto Escobar Estrada** – 00401624
*   **Ricardo Alberto Pineda Hernández** – 00378824
*   **Chelsea Sayuri Mejía Vásquez** – 00164523
*   **Carlos David Vásquez Martínez** – 00063923

---
**Institución:** Universidad Centroamericana José Simeón Cañas (UCA)  
**Ciclo:** 01-2026
