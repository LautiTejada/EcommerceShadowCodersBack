## Script de variables de entorno

Este script (`set-env.ps1`) define todas las variables necesarias para correr el backend localmente.

### Uso

1. Abre una terminal PowerShell en la raíz del proyecto.
2. Ejecuta:
   ```powershell
   .\set-env.ps1
   ./gradlew bootRun
   ```

### Variables definidas

- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- MP_ACCESS_TOKEN
- MP_WEBHOOK_SECRET

> Recuerda: Si cierras la terminal, debes volver a ejecutar el script antes de arrancar el backend.

### Seguridad

No subas este archivo con secretos a producción ni lo compartas públicamente.
