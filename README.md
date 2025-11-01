# 📲 TicketScan

## 📌 Resumen
**TicketScan** es una aplicación móvil desarrollada para **Android** que permite al usuario registrar sus compras de supermercado de diferentes maneras:
- 📷 Tomando una **foto del ticket**.
- ✍️ **Ingresándolas manualmente**.
- 🎙️ **Dictándolas mediante una nota de voz**.

Todos los datos se **normalizan y se guardan en un historial de compras**, al cual el usuario puede acceder en cualquier momento.  
La aplicación también ofrece un **análisis de patrones de compra semanales y mensuales**, ayudando a comprender mejor los hábitos de consumo.

---

## ✅ Requerimientos principales

1. El usuario debe poder **cargar una imagen** de un ticket de supermercado y obtener los datos normalizados y guardados en su historial.
2. El usuario debe poder **cargar una compra manualmente** y guardarla en su historial.
3. El usuario debe poder **consultar su historial de compras**.
4. Los productos registrados en el historial deben ser **clasificados en categorías automáticamente**.
5. El usuario debe poder solicitar un **análisis de patrones de compra** (por semana o por mes), incluyendo:
    - Monto total de compras.
    - Promedio por compra.
    - Distribución por categoría.
    - Comparación con otros períodos.
6. El usuario debe poder **registrar una compra mediante nota de voz**, obteniendo los datos normalizados y guardados.
7. El usuario debe poder **exportar su historial** en formato **PDF** y/o **Excel/CSV**.

---

## 🚀 Tecnologías utilizadas
- **Lenguaje principal:** Kotlin
- **IDE:** Android Studio
- **Base de datos:** SQLite / Room (a definir según implementación)
- **Procesamiento de imágenes:** OCR (para lectura de tickets)
- **Procesamiento de voz:** Reconocimiento de voz de Android
- **Generación de reportes:** Librerías para exportación a PDF y CSV

---

## 📊 Funcionalidades clave
- Registro flexible de compras (**imagen, texto o voz**).
- **Historial organizado y consultable** en todo momento.
- **Clasificación automática** de productos en categorías.
- **Análisis detallado** de gastos y hábitos de consumo.
- **Exportación de historial** en distintos formatos.
- **Notificaciones automáticas** vía Firebase con recordatorios e informes.

---

## 📦 Instalación y ejecución
1. Clonar este repositorio:
   ```bash
   git clone https://github.com/usuario/ticketscan.git
   ```  
2. Abrir el proyecto en **Android Studio**.
3. Conectar un dispositivo Android o configurar un emulador.
4. Ejecutar la aplicación con el botón **Run ▶️**.

---

## 🔔 Notificaciones con Firebase

Para habilitar las notificaciones se requiere configurar Firebase Cloud Messaging:

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/) y agregar la app Android.
2. Descargar el archivo `google-services.json` y ubicarlo en `app/google-services.json`.
3. Habilitar **Cloud Messaging** y, opcionalmente, **Analytics** para el proyecto.
4. Crear dos Cloud Functions (o endpoints equivalentes) con los nombres `syncNotificationPreferences` y `sendTicketScanNotification`. La primera debe registrar el token FCM y los tópicos seleccionados; la segunda debe emitir notificaciones hacia los tópicos `weekly_inactivity`, `weekly_stats` y `monthly_comparison` respetando la estructura del payload enviada por la app.
5. Revisar las reglas de seguridad del backend para proteger los endpoints y las claves de servidor.

El cliente mantiene los recordatorios mediante **WorkManager**, por lo que es necesario mantener los servicios de Google Play actualizados en el dispositivo o emulador de pruebas.

---

## 👥 Autores
Proyecto desarrollado por:
- Mateo Bertogliati
- Carolina Boschini
- Matias Cotens
- Guadalupe Garcia
- Patricio Galli