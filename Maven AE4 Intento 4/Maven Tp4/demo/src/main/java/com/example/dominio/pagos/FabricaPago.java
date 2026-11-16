package com.example.dominio.pagos;

/** Creador del Factory Method: cada implementación decide qué subclase de Pago instanciar. */
public interface FabricaPago {
    Pago crearPago(String numero, String fecha, double monto);
}
