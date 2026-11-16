package com.example.dominio.pagos;

/** Producto concreto: pago en tarjeta (5% de recargo). */
public class PagoTarjeta extends Pago {
    private static final double PORCENTAJE_RECARGO = 0.05;

    public PagoTarjeta(String numero, String fecha, double monto) { super(numero, fecha, monto); }

    @Override
    public String getFormaPago() { return "Tarjeta"; }

    @Override
    public double calcularRecargo(double base) { return base * PORCENTAJE_RECARGO; }
}
