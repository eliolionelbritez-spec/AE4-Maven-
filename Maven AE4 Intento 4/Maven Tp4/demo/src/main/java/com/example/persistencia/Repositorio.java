package com.example.persistencia;

import java.util.List;

/**
 * Contrato de persistencia. Los servicios dependen de esta interfaz y no de cómo se guardan
 * los datos (archivo de texto hoy; base de datos o JSON mañana) sin tocar la lógica del negocio.
 */
public interface Repositorio<T> {

    /** Guarda la colección completa, reemplazando lo almacenado antes. */
    void guardar(List<? extends T> datos) throws PersistenciaException;

    /** Reconstruye los objetos del dominio a partir de lo almacenado. */
    List<T> cargar() throws PersistenciaException;

    /** Devuelve los registros tal como están almacenados, uno por línea (para mostrarlos). */
    List<String> leerRegistros() throws PersistenciaException;
}
