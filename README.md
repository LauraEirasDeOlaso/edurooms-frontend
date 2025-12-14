# 📱 EduRooms Frontend

Aplicación Android para gestión de reservas de aulas y reportes de incidencias.

## 📋 Requisitos

- Android Studio 2022.1+
- Android SDK 31+
- JDK 11+
- Kotlin 1.8+

## 🔧 Instalación
```bash
# Clonar repositorio
git clone https://github.com/LauraEirasDeOlaso/edurooms-frontend.git
cd edurooms-frontend

# Abrir en Android Studio
# File → Open → Seleccionar carpeta
```

## ✨ Features

### Autenticación
- ✅ Login/Logout
- ✅ Remember Me (sesiones persistentes)
- ✅ Cambio de contraseña

### Profesor
- ✅ Ver aulas disponibles
- ✅ Crear y cancelar reservas
- ✅ Ver mis reservas
- ✅ Reportar incidencias
- ✅ Badges de notificaciones (próximas reservas)

### Administrador
- ✅ Gestionar usuarios (CRUD)
- ✅ Gestionar aulas (CRUD)
- ✅ Gestionar reservas
- ✅ Reactivar/traspasar reservas
- ✅ Gestionar incidencias
- ✅ Filtros avanzados
- ✅ Badges de notificaciones (incidencias pendientes)

### UI/UX
- ✅ Diseño material
- ✅ Navegación inferior
- ✅ Calendario con date picker
- ✅ RecyclerView con horarios
- ✅ Color-coded status indicators

## 🏗️ Arquitectura
```
src/main/java/com/edurooms/app/
├── data/
│   ├── models/           - Data classes
│   ├── network/          - Retrofit API
│   └── utils/            - Utilities
├── ui/
│   ├── activities/       - Pantallas
│   ├── adapters/         - RecyclerView adapters
│   └── theme/            - Estilos y colores
└── res/
    ├── layout/           - XML layouts
    ├── values/           - Strings, colors, styles
    └── drawable/         - Iconos y drawables
```

## 🔌 Configuración API

En `Constants.kt`, actualiza `BASE_URL` con tu servidor:
```kotlin
const val BASE_URL = "https://tu-backend-url.com/api/"

Actualmente apunta a: Railway (producción)
```

## 📦 Build & Release

### Debug (Desarrollo)
```
Build → Build Bundles/APKs → Build APK
```

### Release (Distribución)
```
Build → Generate Signed Bundle/APK
→ APK
→ Selecciona keystore
→ Release
```

El APK se genera en:
```
app/release/app-release.apk
```

## 🎨 Paleta de Colores

- **Primary**: Beige topo (`#C8B6A6`)
- **Success**: Verde (`#4CAF50`)
- **Error**: Rojo (`#E53935`)
- **Warning**: Naranja (`#F5A623`)
- **Background**: Blanco roto (`#F9F9F7`)

## 📱 Requisitos de Android

- Mínimo: Android 9 (API 28)
- Target: Android 15 (API 35)

## 🔐 Seguridad

- ✅ JWT tokens en SharedPreferences
- ✅ HTTPS en producción
- ✅ Validación de roles
- ✅ Protección de endpoints

## 📝 Dependencias Principales

- Retrofit 2.9.0
- Gson 2.10.1
- Glide 4.15.1
- Material Components
- AndroidX

## 👨‍💻 Autor

Laura Eiras de Olaso

## 📄 Licencia

MIT

## 🔗 Enlaces útiles

- [Backend Repository](https://github.com/LauraEirasDeOlaso/edurooms-backend)
- [Documentación del proyecto](https://github.com/LauraEirasDeOlaso/edurooms-backend#readme)
- [Android Developer Docs](https://developer.android.com)

---

## 📧 Contacto

Proyecto de ciclo formativo - DAM (Desarrollo de Aplicaciones Multiplataforma)