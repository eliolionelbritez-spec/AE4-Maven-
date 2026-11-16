package com.example.dominio.pagos;

/** Creador concreto: fabrica pagos en efectivo. */
public class FabricaPagoEfectivo implements FabricaPago {
    @Override
    public Pago crearPago(String numero, String fecha, double monto) {
        return new PagoEfectivo(numero, fecha, monto);
    }
}
