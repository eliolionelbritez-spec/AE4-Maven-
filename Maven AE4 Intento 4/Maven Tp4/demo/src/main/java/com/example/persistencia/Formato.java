package com.example.persistencia;

/** Convenciones del formato de línea de los archivos de texto (detalle interno de esta capa). */
final class Formato {
    static final String SEP = ";";
    static final String SEP_LISTA = "|";

    private Formato() { }

    /** Quita los separadores del texto libre para que no rompan el formato de la línea. */
    static String limpiar(String texto) {
        if (texto == null) return "";
        return texto.replace(';', ' ').replace('|', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
