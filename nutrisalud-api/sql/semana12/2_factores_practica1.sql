USE nutrisalud_s11;
GO
CREATE TABLE factor_riesgo (
  id     BIGINT IDENTITY(1,1) NOT NULL
         CONSTRAINT pk_factor_riesgo PRIMARY KEY,
  nombre NVARCHAR(60) NOT NULL
         CONSTRAINT uq_factor_nombre UNIQUE
);
CREATE TABLE paciente_factor (
  paciente_id BIGINT NOT NULL
         CONSTRAINT fk_pf_paciente REFERENCES paciente(id),
  factor_id   BIGINT NOT NULL
         CONSTRAINT fk_pf_factor REFERENCES factor_riesgo(id),
  CONSTRAINT pk_paciente_factor PRIMARY KEY (paciente_id, factor_id)
);
INSERT INTO factor_riesgo (nombre) VALUES
 (N'Bajo peso al nacer'), (N'Prematuridad'), (N'Parasitosis'),
 (N'Sin agua segura en casa');
GO
