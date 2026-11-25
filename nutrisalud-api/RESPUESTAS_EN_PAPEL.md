# respuestas en papel (semana 12, bloque b)

## b1 · a
- con Set: 2 filas (juntos y vaso); el tercer inscribir(juntos) no repite.
- con List: 3 inserts, la tercera repite la pk -> error y rollback -> 0 filas (409).

## b1 · b
- error: joinColumns e inverseJoinColumns al revés (joinColumns = fk propia = paciente_id).
- se guarda la fila paciente_id=3, programa_id=1 (en ssms: thiago en juntos, falso).
- en la app, los programas de thiago salen vacíos (busca programa_id = 3).

## b2
- a: NO (RuntimeException -> rollback)
- b: SÍ (checked, sin rollback por defecto)
- c: NO (rollbackFor)
- d: SÍ (atrapada dentro, nunca cruza el proxy; commit)

## b3
- orden: D F C H A B I
- sobran: E (@Param("alt") no coincide con :altitud) y G (concatenación = inyección jpql)

---------------------------------------------------------------------------

# preguntas escritas de la tarea (para el informe)

## ejercicio 1 (2 líneas)
hibernate ejecutó 1 solo delete (delete from paciente_programa where paciente_id=? and programa_id=?).
no borró todas las filas porque el mapeo es un Set: hibernate sabe qué elemento salió y borra solo esa fila (con una List, un "bag", borraría todas las del paciente y reinsertaría las que quedan).

## ejercicio 2 (4 líneas)
1. una sola petición hace 1 insert en control + 1 insert en paciente_factor por cada factor (con [1, 2]: 3 escrituras), todas en una sola transacción.
2. en A no quedó el control aunque se guardó primero: el insert se ejecutó, pero el factor 99 lanzó RecursoNoEncontradoException (RuntimeException) y spring hizo rollback de toda la transacción.
3. en B quedó todo porque ValorSospechosoException es verificada (extends Exception) y spring solo hace rollback por defecto con RuntimeException y Error: hizo commit, y el 409 se armó después en ManejadorErrores.
4. en C, con rollbackFor = ValorSospechosoException.class, el 409 sí deshace las dos escrituras y no cambia ninguna tabla.

## ejercicio 4 · predicciones
1a DELETE retirar -> 200, lista solo con Juntos
1b repetir -> 409 "no está inscrito"
2a factorIds [1, 99] -> 404, nada cambia
2b hemoglobina 4.0 -> 409, con rollbackFor no queda nada
3a rango 0-5000 -> 200, aparece mi paciente
3b rango 6000-7000 -> 200, lista vacía
4  altitudMin 4000 > altitudMax 3000 -> 409
