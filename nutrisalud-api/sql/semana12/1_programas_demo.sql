USE nutrisalud_s11;            -- tu base de la Semana 11
GO
-- Catálogo: un programa por fila
CREATE TABLE programa_social (
    id      BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_programa_social PRIMARY KEY,
    nombre  NVARCHAR(60) NOT NULL CONSTRAINT uq_programa_nombre UNIQUE
);
-- Tabla intermedia: SOLO las dos claves foráneas
CREATE TABLE paciente_programa (
    paciente_id  BIGINT NOT NULL CONSTRAINT fk_pp_paciente REFERENCES paciente(id),
    programa_id  BIGINT NOT NULL CONSTRAINT fk_pp_programa REFERENCES programa_social(id),
    CONSTRAINT pk_paciente_programa PRIMARY KEY (paciente_id, programa_id)
);
INSERT INTO programa_social (nombre)
VALUES (N'Juntos'), (N'Cuna Más'), (N'Vaso de Leche');      -- ids 1, 2 y 3
GO
