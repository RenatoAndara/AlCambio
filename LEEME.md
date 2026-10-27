# Calculadora BCV — App Android

**Autor:** Renato Andara · Desarrollado por R.A. 2026

Conversión Bs ↔ USD/EUR con la tasa oficial del día (fuente: DolarApi, que replica la tasa del BCV).
La interfaz va dentro del APK, así que abre y calcula sin conexión usando la última tasa guardada.

## Compilar el APK (GitHub Actions, gratis)
1. Crea un repositorio **privado** en github.com (contiene la firma del APK).
2. Sube TODO el contenido de esta carpeta, incluida la carpeta oculta `.github`.
3. Pestaña **Actions** → "Compilar APK" → espera la marca verde (3–6 min).
4. Abre la ejecución → sección **Artifacts** → descarga `CalculadoraBCV-apk` (es un .zip con el .apk).

## Instalar en Android
Pasa el .apk al teléfono, ábrelo y permite "Instalar apps desconocidas" para el navegador o el gestor de archivos.

## Actualizar la app
Edita `app/src/main/assets/index.html`, sube `versionCode` en `app/build.gradle`, haz commit y vuelve a descargar el APK.
Gracias a la firma fija se instala encima de la anterior sin perder la tasa guardada.
No pierdas `app/firma-calculadora.jks`: sin ella no podrás actualizar encima de la versión instalada.
