# Finance 💰

Aplicación Android de finanzas personales enfocada en el ahorro por metas. Registra gastos, ingresos y aportes, organiza tus ahorros en fondos (bancos, billeteras digitales, efectivo) y visualiza tu progreso hacia una meta de ahorro.

## Características

- 🎯 **Meta de ahorro** con anillo de progreso animado y monto restante
- 🏦 **Fondos**: divide tus ahorros entre bancos, billeteras y efectivo; tú decides cuáles suman a la meta
- 💸 **Registro rápido** de gastos, ingresos y aportes con teclado numérico propio
- 🗂️ **Categorías de gasto** con íconos, colores y presupuesto mensual opcional
- 📊 **Análisis mensual**: gráfico de dona por categorías y alertas de presupuesto
- 📅 **Historial por mes**: resumen de ingresos, gastos, aportes y balance
- 🌙 **Modo oscuro** siguiendo el sistema
- 💾 **100% offline**: todos los datos viven en una base SQLite local (Room)

## Stack técnico

| Capa | Tecnología |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Estado | ViewModel + StateFlow |
| Persistencia | Room (SQLite) |
| Preferencias | DataStore Preferences |
| DI | Contenedor manual (`AppContainer`) |

## Arquitectura

Clean Architecture ligera en tres capas dentro de un solo módulo:

```
com.example.finance
├── data
│   ├── local          # Room: entities, DAOs, database
│   ├── preferences    # DataStore
│   └── repository     # Implementaciones de repositorios
├── domain
│   ├── model          # Modelos de dominio
│   └── repository     # Interfaces de repositorios
├── di                 # AppContainer y factory de ViewModels
├── ui
│   ├── components     # Composables reutilizables
│   ├── navigation     # Rutas y bottom bar
│   ├── screens        # Una carpeta por pantalla (screen + ViewModel)
│   └── theme          # Colores, tipografía
└── util               # Formateadores de moneda y fecha
```

Decisiones de diseño:

- **Dinero como `Long`** (pesos enteros), nunca `Double`, para evitar errores de redondeo
- **Fechas como epoch day** en la base de datos, `LocalDate`/`YearMonth` en dominio y UI
- **Flujos reactivos**: la UI observa Room vía `Flow`, todo se actualiza solo
- **El saldo de cada fondo se calcula en SQL** (saldo inicial + aportes), sin estado duplicado

## Ejecutar

Requiere Android Studio (JDK 17+) y un dispositivo o emulador con Android 8.0 (API 26) o superior.

```bash
./gradlew assembleDebug
./gradlew test
```
