package com.example.persistencia;

/** Se pidió leer datos que todavía no fueron guardados. */
public class DatosInexistentesException extends PersistenciaException {
    private static final long serialVersionUID = 1L;

    private final String origen;

    public DatosInexistentesException(String origen, Throwable causa) {
        super("Todavía no hay datos guardados en " + origen, causa);
        this.origen = origen;
    }

    public String getOrigen() { return origen; }
}
