package com.example.dominio.pagos;

import com.example.dominio.excepciones.DatoInvalidoException;

/**
 * Producto del Factory Method. Cada forma de pago es una subclase que define su propio recargo,
 * en lugar de guardar la forma de pago como un texto libre.
 */
public abstract class Pago {
    private String numero;
    private String fecha;
    private double monto;
    private EstadoPago estado = EstadoPago.PENDIENTE;

    protected Pago(String numero, String fecha, double monto) {
        this.numero = numero;
        this.fecha = fecha;
        setMonto(monto);
    }

    /** Nombre de la forma de pago (Efectivo, Transferencia, Tarjeta). */
    public abstract String getFormaPago();

    /** Recargo que esta forma de pago aplica sobre el importe base (subtotal menos descuento). */
    public abstract double calcularRecargo(double base);

    /** Suma un pago posterior al monto ya abonado. */
    public void acreditar(double importe) { setMonto(monto + importe); }

    /** El propio pago decide su estado a partir del total adeudado. */
    public void actualizarEstado(double total) {
        this.estado = EstadoPago.segun(monto, total);
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public double getMonto() { return monto; }
    public final void setMonto(double monto) {
        if (monto < 0) throw new DatoInvalidoException("El monto del pago no puede ser negativo");
        this.monto = monto;
    }
    public EstadoPago getEstado() { return estado; }
}
