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

## 👥 Autores
Proyecto desarrollado por:
- Mateo Bertogliati
- Carolina Boschini
- Matias Cotens
- Guadalupe Garcia
- Patricio Galli
