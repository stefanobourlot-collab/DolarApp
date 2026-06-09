# 📱 DolarApp - Cotizaciones en Tiempo Real 🚀

**DolarApp** es una aplicación nativa de Android diseñada para seguir de cerca el pulso del mercado cambiario en Argentina. Consume datos en tiempo real de una API pública para ofrecer información precisa, limpia y al instante sobre las distintas variantes del dólar y el euro, sumando una calculadora de conversión inteligente.

---

## ✨ Características Principales

* **💵 Pizarra Dinámica:** Visualización en tiempo real de los valores de Compra y Venta para Dólar Oficial, Blue, MEP, CCL, Cripto y Euro.
* **🧮 Convertidor Inteligente:** Calculadora integrada que permite convertir de Pesos (ARS) a cualquier moneda extranjera y viceversa con un solo toque.
* **🔄 Actualización Manual:** Sistema de reintento ante fallos de conexión y barra de progreso de carga fluida (*LinearProgressIndicator*).
* **🎨 Interfaz Moderna:** Diseño limpio, reactivo y adaptativo desarrollado íntegramente con **Jetpack Compose** y componentes de **Material Design 3**.

---

## 🛠️ Tecnologías y Arquitectura Utilizadas

El desarrollo se construyó siguiendo las mejores prácticas recomendadas para el desarrollo móvil moderno en Android:

* **Lenguaje:** Kotlin 💜
* **UI Framework:** Jetpack Compose (Layouts declarativos y reactivos).
* **Arquitectura:** MVVM (Model-View-ViewModel) para una separación limpia de la lógica de negocio y la interfaz de usuario.
* **Manejo de Estados:** Asincronismo nativo mediante *Kotlin Coroutines* y flujos de datos reactivos con *StateFlow*.
* **Conexión a Red:** Retrofit y OkHttp para el consumo eficiente de la API REST de *dolarapi.com*.

---

## 📸 Capturas de Pantalla

| Convertidor Inteligente | Pizarra de Precios |
<img width="1080" height="2340" alt="Captura de pantalla 2026-06-08 234047" src="https://github.com/user-attachments/assets/bdb143cf-c3e1-4c80-b20d-41759df05fa7" />
<img width="1080" height="2340" alt="Captura de pantalla 2026-05-16 204547" src="https://github.com/user-attachments/assets/b4e2ff66-7b10-4a9d-8c40-c281b60708c6" />


---

## 🚀 Instalación y Pruebas

Si querés clonar el proyecto y probarlo en tu entorno local:

1. Cloná este repositorio:
   ```bash
   git clone [https://github.com/stefanobourlot-collab/DolarApp-Android.git](https://github.com/TU_USUARIO/DolarApp-Android.git)
