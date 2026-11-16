package com.example.persistencia;

/** Fallo al guardar o leer datos. Oculta la tecnología concreta (IOException, SQLException, ...). */
public class PersistenciaException extends Exception {
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) { super(mensaje, causa); }
    public PersistenciaException(String mensaje) { super(mensaje); }
}
