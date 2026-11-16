package com.example.dominio.descuentos;

/** Estrategia concreta: 5% de descuento para clientes premiums. */
public class DescuentoPremium implements EstrategiaDescuento {
    private static final double PORCENTAJE = 0.05;

    @Override
    public double calcularDescuento(double subtotal) { return subtotal * PORCENTAJE; }
}
