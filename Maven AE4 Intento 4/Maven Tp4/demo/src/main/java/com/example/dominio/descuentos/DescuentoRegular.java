package com.example.dominio.descuentos;

/** Estrategia concreta: ningún descuento para clientes regulars. */
public class DescuentoRegular implements EstrategiaDescuento {
    private static final double PORCENTAJE = 0.00;

    @Override
    public double calcularDescuento(double subtotal) { return subtotal * PORCENTAJE; }
}
