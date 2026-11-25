# Matriz de pruebas de integración · Semana 9 · NutriSalud

**Estudiante:** ______________________  **Grupo:** ____  **Fecha:** ____________

**Backend usado:** ☐ el de mi grupo (URL: ______________________)  ☐ la API de referencia

Ejecuta cada caso con F12 abierto en la pestaña **Network**. Llena las dos últimas
columnas y pega una captura debajo de la tabla, con el ID del caso como título.
Exporta este documento a PDF y súbelo a Blackboard antes de salir.

| ID | Caso | Pasos | Resultado esperado | Obtenido | ¿Pasa? |
|---|---|---|---|---|---|
| P01 | Lista con datos | Abrir /pacientes | GET 200 y tarjetas en pantalla | | |
| P02 | API apagada | Detener la API y recargar /pacientes | Aviso «No hay conexión con el servidor» | | |
| P03 | Paciente inexistente | Abrir /pacientes/999 | 404 y aviso «El registro no existe…» | | |
| P04 | Ruta protegida | Sin sesión, abrir /pacientes/nuevo | La URL cambia a /login?volver=… | | |
| P05 | Crear válido | Ingresar, llenar y guardar | POST 201, aparece en la lista y en SQL Server | | |
| P06 | Dato inválido | Altitud 6000 y Guardar | Mensaje bajo el campo y ningún POST en Network | | |
| P07 | DNI repetido | DNI 71234567 y Guardar | 409 y la regla del backend en pantalla | | |
| P08 | Token inventado | En la consola: `localStorage.setItem('nutrisalud.token','inventado')`, editar y Guardar | PUT 401 y vuelve al login sin token | | |

## Contrato revisado (antes de las pruebas)

| # | Punto | ¿Cumple? | Qué corregí |
|---|---|---|---|
| 1 | Arranca: /api/pacientes muestra un JSON | | |
| 2 | JSend: `{ "status": "success", "data": [...] }` | | |
| 3 | Campos con los mismos nombres que `dominio/paciente.ts` | | |
| 4 | CORS: origen localhost:4200, GET POST PUT DELETE y el header Authorization | | |
| 5 | Login: POST /api/auth/login devuelve `data: { token, rol }` | | |
| 6 | Códigos: 201 · 200 · 204 · 400 · 401 · 403 · 404 · 409 | | |

## Capturas

### P01

### P02

### P03

### P04

### P05

### P06

### P07

### P08
