package com.example.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/** Acceso de bajo nivel a un archivo de texto (una línea por registro). No conoce el dominio. */
class GestorArchivo {
    private final Path ruta;

    GestorArchivo(String ruta) { this.ruta = Paths.get(ruta); }

    void escribir(List<String> lineas) throws PersistenciaException {
        try {
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo escribir " + ruta + ": " + e.getMessage(), e);
        }
    }

    List<String> leer() throws PersistenciaException {
        try {
            return Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new DatosInexistentesException(ruta.toString(), e);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer " + ruta + ": " + e.getMessage(), e);
        }
    }
}
