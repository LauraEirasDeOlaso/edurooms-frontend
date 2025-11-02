# 📱 EduRooms Frontend

Aplicación móvil nativa en **Kotlin** con **Android Studio** para el sistema de gestión de aulas **EduRooms**.

## 🎯 Funcionalidades

- **Autenticación:** Login y registro con roles (profesor/administrador)
- **Gestión de aulas:** Visualizar aulas disponibles, filtrado y búsqueda
- **Reservas:** Crear, modificar y cancelar reservas de aulas
- **Incidencias:** Reportar y consultar incidencias técnicas
- **Escaneo QR:** Acceso rápido a información de aulas
- **Sugerencias inteligentes:** Horarios óptimos recomendados
- **Sincronización:** Conexión en tiempo real con backend Node.js

---

## 📋 Requisitos

| Herramienta | Versión                     |
|-------------|-----------------------------|
| Android Studio | Otter (2025.2.1) o superior |
| Kotlin | 1.9+                        |
| Gradle | 8.0+                        |
| Android SDK | API 24+ (mínimo)            |
| Java | 21+                         |

---

## 🛠️ Instalación y Configuración

### 1. Clonar repositorio
```bash
git clone https://github.com/LauraEirasDeOlaso/edurooms-frontend.git
cd edurooms-frontend
```

### 2. Abrir en Android Studio

- Abre Android Studio
- Selecciona "Open an Existing Project"
- Navega a la carpeta `edurooms-frontend`
- Espera a que sincronice Gradle

### 3. Configurar conexión al backend

Edita `app/src/main/java/com/edurooms/app/utils/Constants.kt`:
```kotlin
object Constants {
    const val BASE_URL = "http://192.168.X.X:3000/api/"  // Cambiar IP según tu red
    // o para emulador:
    // const val BASE_URL = "http://10.0.2.2:3000/api/"
}
```

### 4. Ejecutar la app

- Conecta un dispositivo Android o inicia el emulador
- Presiona "Run" (Shift + F10) o el botón verde en Android Studio

---

## 📁 Estructura del Proyecto
```
app/
├── src/main/
│   ├── java/com/edurooms/app/
│   │   ├── ui/
│   │   │   ├── activities/      # Pantallas principales
│   │   │   ├── fragments/       # Fragmentos reutilizables
│   │   │   └── adapters/        # Adaptadores para listas
│   │   ├── data/
│   │   │   ├── models/          # Clases de datos
│   │   │   ├── api/             # Retrofit service
│   │   │   └── repository/      # Gestión de datos
│   │   ├── network/
│   │   │   └── RetrofitClient.kt # Configuración Retrofit
│   │   ├── utils/
│   │   │   ├── Constants.kt     # URLs, constantes
│   │   │   └── TokenManager.kt  # Gestión de JWT
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── layout/              # Archivos XML de UI
│   │   ├── drawable/            # Imágenes e iconos
│   │   └── values/              # Colores, strings, estilos
│   └── AndroidManifest.xml
├── build.gradle                 # Dependencias y configuración
└── proguard-rules.pro
```

---

## 🔗 Conexión al Backend

El frontend se conecta al backend Node.js usando **Retrofit**:
```kotlin
// Ejemplo de petición
val api = RetrofitClient.apiService
api.login(email, password).enqueue(object : Callback<LoginResponse> {
    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
        // Manejar respuesta
    }
    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
        // Manejar error
    }
})
```

---

## 🧪 Testing

- Usar **Logcat** en Android Studio para ver logs
- Probar endpoints con **Postman** primero (antes de Android)
- Usar emulador con API 24+

---

## 📦 Dependencias principales

- **Retrofit**: Cliente HTTP
- **OkHttp**: Interceptor para JWT
- **Gson**: Serialización JSON
- **Material Design 3**: Componentes UI
- **ZXing**: Escaneo QR

---

## 🚀 Build y Release

### Debug
```bash
./gradlew assembleDebug
```

### Release
```bash
./gradlew assembleRelease
```

---

## 📝 Notas Importantes

- ⚠️ Cambia la URL del backend según tu entorno (local, servidor, etc.)
- ⚠️ El token JWT se almacena en SharedPreferences (mejorar en futuros sprints)
- ⚠️ La app requiere permisos de INTERNET en AndroidManifest.xml

---

## 🔗 Enlaces útiles

- [Backend Repository](https://github.com/LauraEirasDeOlaso/edurooms-backend)
- [Documentación del proyecto](https://github.com/LauraEirasDeOlaso/edurooms-backend#readme)
- [Android Developer Docs](https://developer.android.com)

---

## 📧 Contacto

Proyecto de ciclo formativo - DAM (Desarrollo de Aplicaciones Multiplataforma)