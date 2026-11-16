package com.example.dominio.actores;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.excepciones.LimiteCreditoExcedidoException;

public class Cliente extends Persona implements Comparable<Cliente> {
    private double limiteCredito;
    private CategoriaCliente categoria;
    private double saldoPendiente = 0;
    private ArrayList<String> historialCompras = new ArrayList<>();

    /** Criterio alternativo (Comparator): por límite de crédito. */
    public static final Comparator<Cliente> POR_LIMITE_CREDITO =
            Comparator.comparingDouble(Cliente::getLimiteCredito);

    public Cliente(String nombre, String domicilio, String dni, String telefono,
                   double limiteCredito, String categoria) {
        super(nombre, domicilio, dni, telefono);
        setLimiteCredito(limiteCredito);
        setCategoria(categoria);
    }

    @Override
    public String getRol() { return "Cliente"; }

    /** Registra una compra; falla si la deuda resultante supera el límite de crédito. */
    public void registrarCompra(String descripcion, double total, double pagado)
            throws LimiteCreditoExcedidoException {
        double deudaResultante = saldoPendiente + (total - pagado);
        if (deudaResultante > limiteCredito)
            throw new LimiteCreditoExcedidoException(getNombre(), limiteCredito, deudaResultante);
        saldoPendiente = deudaResultante;
        historialCompras.add(descripcion);
    }

    /** Un pago posterior reduce la deuda del cliente (nunca por debajo de cero). */
    public void acreditarPago(double importe) { saldoPendiente = Math.max(0, saldoPendiente - importe); }

    /** Orden natural (Comparable): por cantidad de compras. */
    @Override
    public int compareTo(Cliente otro) {
        return Integer.compare(historialCompras.size(), otro.historialCompras.size());
    }


    /** Restaura deuda e historial al recuperar datos guardados (no registra una compra nueva). */
    public void restaurarEstado(double saldo, List<String> historial) {
        if (saldo < 0) throw new DatoInvalidoException("El saldo pendiente no puede ser negativo");
        this.saldoPendiente = saldo;
        this.historialCompras.clear();
        this.historialCompras.addAll(historial);
    }

    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) {
        if (limiteCredito < 0) throw new DatoInvalidoException("El límite de crédito no puede ser negativo");
        this.limiteCredito = limiteCredito;
    }
    public CategoriaCliente getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = CategoriaCliente.desde(categoria); }
    public double getSaldoPendiente() { return saldoPendiente; }
    public ArrayList<String> getHistorialCompras() { return historialCompras; }
    public void agregarHistorialCompra(String compra) { historialCompras.add(compra); }
}