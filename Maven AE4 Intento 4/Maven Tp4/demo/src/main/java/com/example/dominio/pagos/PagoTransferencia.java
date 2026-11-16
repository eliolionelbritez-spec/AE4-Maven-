package com.example.dominio.pagos;

/** Producto concreto: pago en transferencia (sin recargo). */
public class PagoTransferencia extends Pago {
    public PagoTransferencia(String numero, String fecha, double monto) { super(numero, fecha, monto); }

    @Override
    public String getFormaPago() { return "Transferencia"; }

    @Override
    public double calcularRecargo(double base) { return 0; }
}
