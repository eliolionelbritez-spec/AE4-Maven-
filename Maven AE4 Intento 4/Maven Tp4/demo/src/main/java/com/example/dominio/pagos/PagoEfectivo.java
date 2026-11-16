package com.example.dominio.pagos;

/** Producto concreto: pago en efectivo (sin recargo). */
public class PagoEfectivo extends Pago {
    public PagoEfectivo(String numero, String fecha, double monto) { super(numero, fecha, monto); }

    @Override
    public String getFormaPago() { return "Efectivo"; }

    @Override
    public double calcularRecargo(double base) { return 0; }
}
