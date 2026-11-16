package com.example.dominio.pagos;

/** Creador concreto: fabrica pagos en tarjeta. */
public class FabricaPagoTarjeta implements FabricaPago {
    @Override
    public Pago crearPago(String numero, String fecha, double monto) {
        return new PagoTarjeta(numero, fecha, monto);
    }
}
