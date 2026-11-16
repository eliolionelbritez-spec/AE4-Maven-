package com.example.dominio.pagos;

/** Creador concreto: fabrica pagos en transferencia. */
public class FabricaPagoTransferencia implements FabricaPago {
    @Override
    public Pago crearPago(String numero, String fecha, double monto) {
        return new PagoTransferencia(numero, fecha, monto);
    }
}
