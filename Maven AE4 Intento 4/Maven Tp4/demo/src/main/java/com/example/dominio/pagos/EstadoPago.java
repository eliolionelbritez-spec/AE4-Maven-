package com.example.dominio.pagos;

/** Estado de un pago respecto del total de la factura. Reemplaza a los textos sueltos "Completo"/"Parcial"/"Pendiente". */
public enum EstadoPago {
    PENDIENTE("Pendiente"),
    PARCIAL("Parcial"),
    COMPLETO("Completo");

    /** Margen de medio centavo para no fallar por redondeo de decimales. */
    private static final double TOLERANCIA = 0.005;

    private final String etiqueta;

    EstadoPago(String etiqueta) { this.etiqueta = etiqueta; }

    /** Regla de negocio: qué estado corresponde según lo pagado y el total adeudado. */
    public static EstadoPago segun(double pagado, double total) {
        if (pagado + TOLERANCIA >= total) return COMPLETO;
        return pagado > 0 ? PARCIAL : PENDIENTE;
    }

    @Override
    public String toString() { return etiqueta; }
}
