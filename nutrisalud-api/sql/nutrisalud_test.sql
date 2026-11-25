/* S14 · práctica 3: base de pruebas nutrisalud_test (mismas tablas, casi vacía).
   Ejecutar en SSMS (F5). Sin contraseñas escritas. */
SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO
USE master;
GO
IF DB_ID(N'nutrisalud_test') IS NULL
    CREATE DATABASE nutrisalud_test;
GO
USE nutrisalud_test;
GO
-- solo si existe el login nutrisalud_app (con sa se salta)
IF SUSER_ID(N'nutrisalud_app') IS NOT NULL AND DATABASE_PRINCIPAL_ID(N'nutrisalud_app') IS NULL
    CREATE USER nutrisalud_app FOR LOGIN nutrisalud_app;
GO
IF DATABASE_PRINCIPAL_ID(N'nutrisalud_app') IS NOT NULL
    GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO nutrisalud_app;
GO

DROP TABLE IF EXISTS paciente_factor;
DROP TABLE IF EXISTS factor_riesgo;
DROP TABLE IF EXISTS paciente_programa;
DROP TABLE IF EXISTS programa_social;
DROP TABLE IF EXISTS control;
DROP TABLE IF EXISTS paciente;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;
GO

CREATE TABLE rol (
    id      BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_rol PRIMARY KEY,
    nombre  NVARCHAR(30) NOT NULL
        CONSTRAINT uq_rol_nombre UNIQUE
        CONSTRAINT ck_rol_nombre CHECK (nombre IN ('PERSONAL_DE_SALUD', 'CIUDADANO'))
);
CREATE TABLE usuario (
    id             BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_usuario PRIMARY KEY,
    email          NVARCHAR(120) NOT NULL CONSTRAINT uq_usuario_email UNIQUE,
    password_hash  NVARCHAR(200) NOT NULL,
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
    dni               CHAR(8) NULL CONSTRAINT ck_paciente_dni CHECK (dni NOT LIKE '%[^0-9]%'),
    fecha_nacimiento  DATE NOT NULL,
    sexo              NCHAR(1) NULL CONSTRAINT ck_paciente_sexo CHECK (sexo IN ('M', 'F')),
    altitud_msnm      INT NOT NULL CONSTRAINT df_paciente_altitud DEFAULT 0
        CONSTRAINT ck_paciente_altitud CHECK (altitud_msnm BETWEEN 0 AND 5000),
    activo            BIT NOT NULL CONSTRAINT df_paciente_activo DEFAULT 1
);
CREATE TABLE control (
    id           BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_control PRIMARY KEY,
    paciente_id  BIGINT NOT NULL,
    fecha        DATE NOT NULL,
    peso         DECIMAL(5,2) NULL CONSTRAINT ck_control_peso  CHECK (peso BETWEEN 0.5 AND 40),
    talla        DECIMAL(5,2) NULL CONSTRAINT ck_control_talla CHECK (talla BETWEEN 30 AND 130),
    hemoglobina  DECIMAL(4,2) NULL CONSTRAINT ck_control_hb    CHECK (hemoglobina BETWEEN 3 AND 25),
    CONSTRAINT fk_control_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);
CREATE TABLE programa_social (
    id      BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_programa_social PRIMARY KEY,
    nombre  NVARCHAR(60) NOT NULL CONSTRAINT uq_programa_nombre UNIQUE
);
CREATE TABLE paciente_programa (
    paciente_id  BIGINT NOT NULL CONSTRAINT fk_pp_paciente REFERENCES paciente(id),
    programa_id  BIGINT NOT NULL CONSTRAINT fk_pp_programa REFERENCES programa_social(id),
    CONSTRAINT pk_paciente_programa PRIMARY KEY (paciente_id, programa_id)
);
CREATE TABLE factor_riesgo (
    id     BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_factor_riesgo PRIMARY KEY,
    nombre NVARCHAR(60) NOT NULL CONSTRAINT uq_factor_nombre UNIQUE
);
CREATE TABLE paciente_factor (
    paciente_id BIGINT NOT NULL CONSTRAINT fk_pf_paciente REFERENCES paciente(id),
    factor_id   BIGINT NOT NULL CONSTRAINT fk_pf_factor REFERENCES factor_riesgo(id),
    CONSTRAINT pk_paciente_factor PRIMARY KEY (paciente_id, factor_id)
);
CREATE UNIQUE INDEX ux_paciente_dni ON paciente(dni) WHERE dni IS NOT NULL;
GO

DECLARE @rolPersonal BIGINT;
INSERT INTO rol (nombre) VALUES ('PERSONAL_DE_SALUD');  SET @rolPersonal = SCOPE_IDENTITY();
INSERT INTO rol (nombre) VALUES ('CIUDADANO');
-- hash BCrypt (no la clave): el mismo usuario de práctica de nutrisalud_s11
INSERT INTO usuario (email, password_hash, estado, rol_id)
VALUES ('enfermera@posta.pe', '$2a$10$0OhzoBwXyBMwTK.mFpvp9uhIQU6BU.8TiSQEBd.f.Lw8a3iZBvXIW', 'ACTIVO', @rolPersonal);
INSERT INTO programa_social (nombre) VALUES (N'Juntos'), (N'Cuna Más'), (N'Vaso de Leche');
INSERT INTO factor_riesgo (nombre) VALUES
 (N'Bajo peso al nacer'), (N'Prematuridad'), (N'Parasitosis'), (N'Sin agua segura en casa');
GO

SELECT (SELECT COUNT(*) FROM usuario) AS usuarios, (SELECT COUNT(*) FROM paciente) AS pacientes;
GO
