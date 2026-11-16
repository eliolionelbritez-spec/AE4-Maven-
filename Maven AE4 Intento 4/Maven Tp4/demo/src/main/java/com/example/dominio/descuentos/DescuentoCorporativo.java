package com.example.dominio.descuentos;

/** Estrategia concreta: 10% de descuento para clientes corporativos. */
public class DescuentoCorporativo implements EstrategiaDescuento {
    private static final double PORCENTAJE = 0.10;

    @Override
    public double calcularDescuento(double subtotal) { return subtotal * PORCENTAJE; }
}
