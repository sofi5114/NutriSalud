USE nutrisalud_s11;
GO
SELECT * FROM paciente_programa WHERE paciente_id = 540;

DELETE FROM paciente_factor WHERE paciente_id = 540;
SELECT COUNT(*) AS controles FROM control WHERE paciente_id = 540;
SELECT COUNT(*) AS factores  FROM paciente_factor WHERE paciente_id = 540;

SELECT p.id, p.nombres, p.apellidos, p.altitud_msnm
FROM paciente p
JOIN paciente_programa pp ON pp.paciente_id = p.id
WHERE pp.programa_id = 1
  AND p.activo = 1
  AND p.altitud_msnm BETWEEN 0 AND 5000
ORDER BY p.altitud_msnm DESC, p.apellidos ASC, p.id ASC;
GO
