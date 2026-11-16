package com.example.presentacion;

import java.util.List;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.actores.Cliente;
import com.example.dominio.pagos.Factura;

/** Salida por consola: cómo se ve cada dato. El dominio no imprime nada, lo imprime esta clase. */
public class Pantalla {

    public void mostrarFactura(Factura f) {
        System.out.println("Factura Número: " + f.getNumero());
        System.out.println("Fecha de Emisión: " + f.getFecha());
        System.out.println("Cliente: " + f.getCliente().getNombre() + " (" + f.getCliente().getCategoria() + ")");
        System.out.println("Historial de compras: " + f.getCliente().getHistorialCompras());
        System.out.println("Empleado Gestor: " + f.getEmpleado().getNombre() + " - Puesto: " + f.getEmpleado().getPuesto());
        System.out.println("Departamento: " + f.getEmpleado().getDepartamento().getNombre());
        System.out.println("Items Adquiridos:");
        for (OfertaComercial item : f.getItems())
            System.out.println(" - " + item.obtenerDetalle() + " - Precio: $" + item.calcularPrecioFinal());
        if (f.calcularDescuento() > 0) {
            System.out.println("Subtotal: $" + f.calcularSubtotal());
            System.out.println("Descuento cliente " + f.getCliente().getCategoria() + ": -$" + f.calcularDescuento());
        }
        if (f.calcularRecargo() > 0)
            System.out.println("Recargo " + f.getPago().getFormaPago() + ": +$" + f.calcularRecargo());
        System.out.println("Forma de Pago: " + f.getPago().getFormaPago());
        System.out.println("Estado del Pago: " + f.getPago().getEstado());
        System.out.println("Total a Pagar: $" + f.calcularTotal());
    }

    public void listarClientes(List<Cliente> lista) {
        for (Cliente c : lista)
            System.out.println("  " + c.getNombre() + " | " + c.getCategoria() + " | límite $" + c.getLimiteCredito()
                    + " | deuda $" + c.getSaldoPendiente() + " | compras " + c.getHistorialCompras().size());
    }

    public void resumenFacturas(String titulo, List<Factura> lista) {
        System.out.println(titulo + ":");
        for (Factura f : lista)
            System.out.println("  " + f.getNumero() + " | " + f.getFecha() + " | " + f.getCliente().getNombre()
                    + " | $" + f.calcularTotal());
    }

    public void listarRegistros(List<String> registros) {
        registros.forEach(l -> System.out.println("  " + l));
    }
}
