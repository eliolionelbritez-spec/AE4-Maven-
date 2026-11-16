package com.example.dominio.descuentos;

/** Strategy: política de descuento aplicable al subtotal de una factura. */
public interface EstrategiaDescuento {

    /** Importe a descontar (no el total final) para el subtotal dado. */
    double calcularDescuento(double subtotal);
}
