/* ============================================================================
   NutriSalud · Base de datos de las semanas 11 y 12
   SIST1402A · Soluciones Web y Aplicaciones Distribuidas · UPN

   Crea la base nutrisalud_s11 con:
     · el usuario de la aplicación  nutrisalud_app  (nunca se usa "sa" en una app)
     · las tablas rol, usuario, paciente y control
     · los 5 niños de siempre (Mateo, Luana, Thiago, Sofía y Valentina, ids 1 al 5)
     · 80 000 pacientes en total, generados para la campaña de tamizaje,
       con sus controles de peso, talla y hemoglobina

   Las tablas de la Semana 12 (programa_social, paciente_programa, factor_riesgo y
   paciente_factor) NO se crean aquí: las creas tú en clase con los scripts de
   sql/semana12/. Este script sí las borra, para que puedas volver a empezar de cero.

   Cómo usarlo
     1. Ábrelo en SQL Server Management Studio (o Azure Data Studio) conectado
        como administrador y ejecútalo completo (F5). Tarda unos segundos.
     2. Puedes volver a ejecutarlo cuando quieras: borra las tablas y deja los
        datos como al inicio. Los datos generados son SIEMPRE los mismos.
     3. ¿Quieres otra cantidad? Cambia @total en la sección 6
        (5 = solo los niños de referencia; máximo 1 000 000).

   Las fechas se calculan desde HOY: las edades de los niños siempre coinciden.

   Usuario de práctica de la API:  enfermera@posta.pe  (la clave te la da el docente)

   Semana 14: la clave del login nutrisalud_app ya NO está escrita aquí.
   Si el login aún no existe: en SSMS activa Consulta > Modo SQLCMD y escribe como primera
   línea (sin guardar el archivo):   :setvar CLAVE_APP "tu_clave"
   Si el login ya existe de semanas anteriores, no hace falta: esa parte se salta.
   ============================================================================ */

SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;     -- lo exige el índice filtrado del DNI (sección 7)
GO

/* ----------------------------------------------------------------------------
   1. Base de datos
   ---------------------------------------------------------------------------- */
USE master;
GO
IF DB_ID(N'nutrisalud_s11') IS NULL
    CREATE DATABASE nutrisalud_s11;
GO

/* ----------------------------------------------------------------------------
   2. Usuario de la aplicación (el mismo de la API de referencia)
      Requiere que SQL Server acepte "Autenticación de SQL Server y de Windows"
      (propiedades del servidor > Seguridad). Si el login ya existe de semanas
      anteriores, se reutiliza con la clave que ya tenía.
   ---------------------------------------------------------------------------- */
IF SUSER_ID(N'nutrisalud_app') IS NULL
    CREATE LOGIN nutrisalud_app WITH PASSWORD = N'$(CLAVE_APP)',
        CHECK_POLICY = ON, DEFAULT_DATABASE = nutrisalud_s11;
GO

USE nutrisalud_s11;
GO
IF DATABASE_PRINCIPAL_ID(N'nutrisalud_app') IS NULL
    CREATE USER nutrisalud_app FOR LOGIN nutrisalud_app;
GO
-- Solo leer y escribir datos: la app no puede crear ni borrar tablas
GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO nutrisalud_app;
GO

/* ----------------------------------------------------------------------------
   3. Limpieza: orden inverso a las dependencias, para poder re-ejecutar.
      Primero las tablas de la Semana 12, que apuntan a paciente.
   ---------------------------------------------------------------------------- */
DROP TABLE IF EXISTS paciente_factor;
DROP TABLE IF EXISTS factor_riesgo;
DROP TABLE IF EXISTS paciente_programa;
DROP TABLE IF EXISTS programa_social;
DROP TABLE IF EXISTS control;
DROP TABLE IF EXISTS paciente;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;
GO

/* ----------------------------------------------------------------------------
   4. Tablas (mismos nombres de tabla y columna que la API)
   ---------------------------------------------------------------------------- */
CREATE TABLE rol (
    id      BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_rol PRIMARY KEY,
    nombre  NVARCHAR(30) NOT NULL
        CONSTRAINT uq_rol_nombre UNIQUE
        CONSTRAINT ck_rol_nombre CHECK (nombre IN ('PERSONAL_DE_SALUD', 'CIUDADANO'))
);

CREATE TABLE usuario (
    id             BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_usuario PRIMARY KEY,
    email          NVARCHAR(120) NOT NULL CONSTRAINT uq_usuario_email UNIQUE,
    password_hash  NVARCHAR(200) NOT NULL,          -- hash BCrypt: la clave nunca se guarda en claro
    estado         NVARCHAR(15)  NOT NULL CONSTRAINT df_usuario_estado DEFAULT 'ACTIVO'
        CONSTRAINT ck_usuario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    rol_id         BIGINT NOT NULL,
    creado_en      DATETIME2(0) NOT NULL CONSTRAINT df_usuario_creado DEFAULT SYSDATETIME(),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
);

CREATE TABLE paciente (
    id                BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_paciente PRIMARY KEY,
    nombres           NVARCHAR(80) NOT NULL,
    apellidos         NVARCHAR(80) NOT NULL,
    dni               CHAR(8) NULL                   -- NULL = DNI en trámite
        CONSTRAINT ck_paciente_dni CHECK (dni NOT LIKE '%[^0-9]%'),
    fecha_nacimiento  DATE NOT NULL,
    sexo              NCHAR(1) NULL CONSTRAINT ck_paciente_sexo CHECK (sexo IN ('M', 'F')),
    altitud_msnm      INT NOT NULL CONSTRAINT df_paciente_altitud DEFAULT 0
        CONSTRAINT ck_paciente_altitud CHECK (altitud_msnm BETWEEN 0 AND 5000),
    activo            BIT NOT NULL CONSTRAINT df_paciente_activo DEFAULT 1    -- baja lógica
);

CREATE TABLE control (
    id           BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_control PRIMARY KEY,
    paciente_id  BIGINT NOT NULL,
    fecha        DATE NOT NULL,
    peso         DECIMAL(5,2) NULL CONSTRAINT ck_control_peso  CHECK (peso BETWEEN 0.5 AND 40),         -- kg
    talla        DECIMAL(5,2) NULL CONSTRAINT ck_control_talla CHECK (talla BETWEEN 30 AND 130),       -- cm
    hemoglobina  DECIMAL(4,2) NULL CONSTRAINT ck_control_hb    CHECK (hemoglobina BETWEEN 3 AND 25),   -- g/dL
    CONSTRAINT fk_control_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);
GO

/* ----------------------------------------------------------------------------
   5. Datos de referencia: los mismos de las semanas 7 a 11
   ---------------------------------------------------------------------------- */
DECLARE @hoy DATE = CAST(GETDATE() AS DATE);
DECLARE @rolPersonal BIGINT;
DECLARE @mateo BIGINT, @luana BIGINT, @thiago BIGINT, @sofia BIGINT, @valentina BIGINT;

INSERT INTO rol (nombre) VALUES ('PERSONAL_DE_SALUD');  SET @rolPersonal = SCOPE_IDENTITY();
INSERT INTO rol (nombre) VALUES ('CIUDADANO');

-- Hash BCrypt de la clave de práctica (el hash no revela la clave)
INSERT INTO usuario (email, password_hash, estado, rol_id)
VALUES ('enfermera@posta.pe', '$2a$10$0OhzoBwXyBMwTK.mFpvp9uhIQU6BU.8TiSQEBd.f.Lw8a3iZBvXIW', 'ACTIVO', @rolPersonal);

-- Mateo = 1, Luana = 2, Thiago = 3, Sofía = 4, Valentina = 5
INSERT INTO paciente (nombres, apellidos, dni, fecha_nacimiento, sexo, altitud_msnm)
VALUES (N'Mateo', N'Rios Vega', '71234567', DATEADD(DAY, -3, DATEADD(MONTH, -18, @hoy)), N'M', 65);
SET @mateo = SCOPE_IDENTITY();
INSERT INTO paciente (nombres, apellidos, dni, fecha_nacimiento, sexo, altitud_msnm)
VALUES (N'Luana', N'Quispe Soto', '72345678', DATEADD(DAY, -3, DATEADD(MONTH, -30, @hoy)), N'F', 34);
SET @luana = SCOPE_IDENTITY();
INSERT INTO paciente (nombres, apellidos, dni, fecha_nacimiento, sexo, altitud_msnm)
VALUES (N'Thiago', N'Alva Mendoza', '73456789', DATEADD(DAY, -3, DATEADD(MONTH, -24, @hoy)), N'M', 2641);
SET @thiago = SCOPE_IDENTITY();
INSERT INTO paciente (nombres, apellidos, dni, fecha_nacimiento, sexo, altitud_msnm)
VALUES (N'Sofía', N'Huamán Torres', NULL, DATEADD(DAY, -3, DATEADD(MONTH, -15, @hoy)), N'F', 65);
SET @sofia = SCOPE_IDENTITY();
INSERT INTO paciente (nombres, apellidos, dni, fecha_nacimiento, sexo, altitud_msnm)
VALUES (N'Valentina', N'Paredes Cruz', '74567890', DATEADD(DAY, -3, DATEADD(MONTH, -4, @hoy)), N'F', 34);
SET @valentina = SCOPE_IDENTITY();

-- Sofía no tiene controles: su tarjeta dice "Sin controles registrados"
INSERT INTO control (paciente_id, fecha, peso, talla, hemoglobina) VALUES
 (@mateo,     DATEADD(DAY, -95, @hoy),  9.80, 76.0, 10.1),
 (@mateo,     DATEADD(DAY,  -8, @hoy), 10.40, 79.5,  9.8),
 (@luana,     DATEADD(DAY, -25, @hoy), 13.10, 90.2, 11.4),
 (@thiago,    DATEADD(DAY, -33, @hoy), 11.00, 84.0,  6.9),
 (@valentina, DATEADD(DAY,  -5, @hoy),  6.20, 61.0,  9.0);
GO

/* ----------------------------------------------------------------------------
   6. Carga masiva: la campaña de tamizaje
      Todo se genera con UNA instrucción INSERT ... SELECT (no hay 80 000 líneas
      de INSERT). Cada niño n recibe números "al azar" con un generador
      congruencial (Lehmer): h = h × 48271 mod 2147483647. Como la fórmula es
      fija, el resultado es el mismo en todas las computadoras.
   ---------------------------------------------------------------------------- */
DECLARE @total INT = 80000;                  -- pacientes en total, incluidos los 5 de referencia
DECLARE @hoy DATE = CAST(GETDATE() AS DATE);
DECLARE @generados INT = CASE WHEN @total > 5 THEN @total - 5 ELSE 0 END;

WITH digito(v) AS (
    SELECT v FROM (VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9)) AS t(v)
),
numero(n) AS (                                   -- 1, 2, 3, ... @generados
    SELECT TOP (@generados) CAST(ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS BIGINT)
    FROM digito d1 CROSS JOIN digito d2 CROSS JOIN digito d3
         CROSS JOIN digito d4 CROSS JOIN digito d5 CROSS JOIN digito d6
),
nombre(sexo, i, nombre) AS (
    SELECT sexo, i, nombre FROM (VALUES
        (N'F', 0,N'Sofía'), (N'F', 1,N'Valentina'), (N'F', 2,N'Camila'), (N'F', 3,N'Luana'), (N'F', 4,N'Isabella'),
        (N'F', 5,N'Mía'), (N'F', 6,N'Valeria'), (N'F', 7,N'Ximena'), (N'F', 8,N'Alessia'), (N'F', 9,N'Emma'),
        (N'F',10,N'Mariana'), (N'F',11,N'Zoe'), (N'F',12,N'Antonella'), (N'F',13,N'Luciana'), (N'F',14,N'Abigail'),
        (N'F',15,N'Danna'), (N'F',16,N'Kiara'), (N'F',17,N'Fernanda'), (N'F',18,N'Renata'), (N'F',19,N'Ariana'),
        (N'F',20,N'Daniela'), (N'F',21,N'Nicole'), (N'F',22,N'Fabiana'), (N'F',23,N'Gabriela'), (N'F',24,N'Andrea'),
        (N'F',25,N'Ana'), (N'F',26,N'María'), (N'F',27,N'Rosa'), (N'F',28,N'Lucía'), (N'F',29,N'Victoria'),
        (N'F',30,N'Allison'), (N'F',31,N'Brianna'), (N'F',32,N'Celeste'), (N'F',33,N'Dayana'), (N'F',34,N'Elena'),
        (N'F',35,N'Flor'), (N'F',36,N'Grecia'), (N'F',37,N'Jazmín'), (N'F',38,N'Kimberly'), (N'F',39,N'Milagros'),
        (N'M', 0,N'Mateo'), (N'M', 1,N'Thiago'), (N'M', 2,N'Santiago'), (N'M', 3,N'Liam'), (N'M', 4,N'Gael'),
        (N'M', 5,N'Sebastián'), (N'M', 6,N'Dylan'), (N'M', 7,N'Joaquín'), (N'M', 8,N'Adrián'), (N'M', 9,N'Ian'),
        (N'M',10,N'Benjamín'), (N'M',11,N'Emiliano'), (N'M',12,N'Lucas'), (N'M',13,N'Daniel'), (N'M',14,N'Diego'),
        (N'M',15,N'Fabricio'), (N'M',16,N'Gabriel'), (N'M',17,N'Jesús'), (N'M',18,N'Juan'), (N'M',19,N'Leonardo'),
        (N'M',20,N'Luis'), (N'M',21,N'Marcelo'), (N'M',22,N'Martín'), (N'M',23,N'Matías'), (N'M',24,N'Nicolás'),
        (N'M',25,N'Rodrigo'), (N'M',26,N'Samuel'), (N'M',27,N'Valentino'), (N'M',28,N'Alonso'), (N'M',29,N'Bruno'),
        (N'M',30,N'Carlos'), (N'M',31,N'Dante'), (N'M',32,N'Eduardo'), (N'M',33,N'Franco'), (N'M',34,N'Hugo'),
        (N'M',35,N'Iker'), (N'M',36,N'Jhon'), (N'M',37,N'Kevin'), (N'M',38,N'Miguel'), (N'M',39,N'Piero')
    ) AS t(sexo, i, nombre)
),
apellido(i, apellido) AS (
    SELECT i, apellido FROM (VALUES
        ( 0,N'Alva'), ( 1,N'Mendoza'), ( 2,N'Rios'), ( 3,N'Vega'), ( 4,N'Quispe'), ( 5,N'Soto'),
        ( 6,N'Huamán'), ( 7,N'Torres'), ( 8,N'Paredes'), ( 9,N'Cruz'), (10,N'Flores'), (11,N'Rodríguez'),
        (12,N'Sánchez'), (13,N'García'), (14,N'Rojas'), (15,N'Díaz'), (16,N'Chávez'), (17,N'Ramos'),
        (18,N'Vásquez'), (19,N'Castillo'), (20,N'Ramírez'), (21,N'Gonzales'), (22,N'Espinoza'), (23,N'Gutiérrez'),
        (24,N'Pérez'), (25,N'López'), (26,N'Salazar'), (27,N'Reyes'), (28,N'Cárdenas'), (29,N'Romero'),
        (30,N'Mamani'), (31,N'Condori'), (32,N'Ccahuana'), (33,N'Tello'), (34,N'Moreno'), (35,N'Herrera'),
        (36,N'Aguilar'), (37,N'Silva'), (38,N'Medina'), (39,N'Guerrero'), (40,N'Cabrera'), (41,N'Benites'),
        (42,N'Rivera'), (43,N'Morales'), (44,N'Castro'), (45,N'Ortiz'), (46,N'Polo'), (47,N'Zavaleta'),
        (48,N'Alvarado'), (49,N'Rubio'), (50,N'Lozano'), (51,N'Horna'), (52,N'Valverde'), (53,N'Asmat'),
        (54,N'Cotrina'), (55,N'Plasencia'), (56,N'Gamboa'), (57,N'Sifuentes'), (58,N'Cerna'), (59,N'Ruiz')
    ) AS t(i, apellido)
),
altitud(i, msnm) AS (                            -- La Libertad: costa (más peso) y sierra
    SELECT i, msnm FROM (VALUES
        (0,34), (1,34), (2,65), (3,10), (4,89), (5,2641), (6,3115), (7,3169)
    ) AS t(i, msnm)
),
semilla AS (                                     -- nueve números pseudoaleatorios por niño
    SELECT n.n, c1.h1, c2.h2, c3.h3, c4.h4, c5.h5, c6.h6, c7.h7, c8.h8, c9.h9
    FROM numero n
    CROSS APPLY (SELECT n.n   * 48271 % 2147483647 AS h1) c1
    CROSS APPLY (SELECT c1.h1 * 48271 % 2147483647 AS h2) c2
    CROSS APPLY (SELECT c2.h2 * 48271 % 2147483647 AS h3) c3
    CROSS APPLY (SELECT c3.h3 * 48271 % 2147483647 AS h4) c4
    CROSS APPLY (SELECT c4.h4 * 48271 % 2147483647 AS h5) c5
    CROSS APPLY (SELECT c5.h5 * 48271 % 2147483647 AS h6) c6
    CROSS APPLY (SELECT c6.h6 * 48271 % 2147483647 AS h7) c7
    CROSS APPLY (SELECT c7.h7 * 48271 % 2147483647 AS h8) c8
    CROSS APPLY (SELECT c8.h8 * 48271 % 2147483647 AS h9) c9
)
INSERT INTO paciente (nombres, apellidos, dni, fecha_nacimiento, sexo, altitud_msnm, activo)
SELECT
    n1.nombre + ISNULL(N' ' + n2.nombre, N''),                       -- 1 de cada 3 tiene dos nombres
    a1.apellido + N' ' + a2.apellido,
    CASE WHEN s.h8 % 40 = 0 THEN NULL                                 -- 2,5 %: DNI en trámite
         ELSE CAST(80000000 + s.n * 7919 % 1000000 AS CHAR(8)) END,   -- 8 dígitos, nunca se repite
    DATEADD(DAY, -CAST(30 + s.h6 % 1770 AS INT), @hoy),              -- de 1 a 59 meses
    x.sexo,
    alt.msnm,
    CASE WHEN s.h9 % 50 = 0 THEN 0 ELSE 1 END                         -- 2 %: dados de baja
FROM semilla s
CROSS APPLY (SELECT CASE WHEN s.h1 % 2 = 0 THEN N'F' ELSE N'M' END AS sexo) x
JOIN nombre n1        ON n1.sexo = x.sexo AND n1.i = s.h2 % 40
LEFT JOIN nombre n2   ON n2.sexo = x.sexo AND s.h3 % 3 = 0
                     AND n2.i = (s.h3 / 3) % 40 AND n2.i <> n1.i
JOIN apellido a1      ON a1.i = s.h4 % 60
JOIN apellido a2      ON a2.i = s.h5 % 60
JOIN altitud alt      ON alt.i = s.h7 % 8
ORDER BY s.n;                                    -- los ids salen en orden: el niño n tiene id n + 5

-- Controles de los niños generados: de 0 a 3 por niño, cada 90 días.
-- El más reciente es de los últimos 60 días. Nunca antes de que el niño naciera.
INSERT INTO control (paciente_id, fecha, peso, talla, hemoglobina)
SELECT
    p.id,
    f.fecha,
    CAST(3.3 + m.meses * 0.22 + (q.q / 1000 % 21 - 10) / 10.0 AS DECIMAL(5,2)),      -- kg
    CAST(50 + m.meses * 0.95 + (q.q / 100000 % 41 - 20) / 10.0 AS DECIMAL(5,2)),     -- cm
    CAST(CASE WHEN q.q % 25 = 0 THEN 6.5 + (q.q / 25 % 15) / 10.0                     -- 4 %: Hb muy baja
              ELSE 9.5 + (q.q / 25 % 46) / 10.0 END AS DECIMAL(4,2))                  -- g/dL
FROM paciente p
CROSS APPLY (SELECT p.id  * 69621 % 2147483647 AS g1) c1
CROSS APPLY (SELECT c1.g1 * 69621 % 2147483647 AS g2) c2
CROSS APPLY (SELECT c2.g2 * 69621 % 2147483647 AS g3) c3
CROSS JOIN (VALUES (1),(2),(3)) AS k(k)                                               -- 1 = el más reciente
CROSS APPLY (SELECT DATEADD(DAY, -CAST((k.k - 1) * 90 + c2.g2 % 60 AS INT), @hoy) AS fecha) f
CROSS APPLY (SELECT DATEDIFF(DAY, p.fecha_nacimiento, f.fecha) / 30 AS meses) m
CROSS APPLY (SELECT (c3.g3 + k.k * 104729) * 48271 % 2147483647 AS q) q
WHERE p.id > 5
  AND k.k <= CASE WHEN c1.g1 % 10 = 0 THEN 0 ELSE 1 + c1.g1 % 3 END              -- 10 %: sin controles
  AND f.fecha >= p.fecha_nacimiento;
GO

/* ----------------------------------------------------------------------------
   7. Índices (se crean DESPUÉS de cargar: así la carga es más rápida)
      Un UNIQUE normal en SQL Server admite UN SOLO NULL. Como muchos niños no
      tienen DNI aún, la unicidad del DNI se declara con un índice FILTRADO.
   ---------------------------------------------------------------------------- */
CREATE UNIQUE INDEX ux_paciente_dni           ON paciente(dni) WHERE dni IS NOT NULL;
CREATE INDEX ix_paciente_activo_apellidos     ON paciente(activo, apellidos, nombres);   -- lista ordenada
CREATE INDEX ix_control_paciente_fecha        ON control(paciente_id, fecha DESC);       -- último control
GO

/* ----------------------------------------------------------------------------
   8. Comprobación: lo que debe salir en la pestaña Resultados
   ---------------------------------------------------------------------------- */
SELECT
    (SELECT COUNT(*) FROM paciente)                  AS pacientes,
    (SELECT COUNT(*) FROM paciente WHERE activo = 1) AS activos,
    (SELECT COUNT(*) FROM paciente WHERE dni IS NULL) AS sin_dni,
    (SELECT COUNT(*) FROM control)                   AS controles;

SELECT TOP (5) id, nombres, apellidos, dni, fecha_nacimiento, altitud_msnm
FROM paciente
ORDER BY id;
GO
