package com.example.dominio.excepciones;

/** Dato de entrada inválido (no checked: es un error de programación/carga). */
public class DatoInvalidoException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) { super(mensaje); }
}
