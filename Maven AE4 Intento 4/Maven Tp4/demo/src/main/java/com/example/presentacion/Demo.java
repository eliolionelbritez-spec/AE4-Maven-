package com.example.presentacion;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.interfaces.Valorizable;
import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.excepciones.FacturaSinItemsException;
import com.example.dominio.excepciones.LimiteCreditoExcedidoException;
import com.example.dominio.excepciones.PagoExcedidoException;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.actores.Persona;
import com.example.dominio.pagos.Factura;
import com.example.persistencia.PersistenciaException;
import com.example.servicio.ServicioClientes;
import com.example.servicio.ServicioFacturacion;
import com.example.servicio.ServicioPersistencia;

/** Recorrido automático (sin menú) que muestra las reglas del negocio, a través de la capa de servicio. */
public class Demo {
    public static void main(String[] args) {
        Aplicacion app = new Aplicacion();
        ServicioClientes clientes = app.clientes();
        ServicioFacturacion facturas = app.facturas();
        Pantalla pantalla = new Pantalla();

        Cliente laura = cliente(clientes, "Laura");
        Cliente marcos = cliente(clientes, "Marcos");
        Empleado juan = empleado(facturas, "Juan");
        Empleado pedro = empleado(facturas, "Pedro");
        OfertaComercial laptop = oferta(facturas, "P001");
        OfertaComercial impresora = oferta(facturas, "P002");
        OfertaComercial soporte = oferta(facturas, "S001");

        // Validación de datos
        try {
            clientes.registrar("Ana", "Domicilio", "DNI123", "5555555555", 1000, "Regular");
        } catch (DatoInvalidoException e) {
            System.out.println("[Dato inválido] " + e.getMessage());
        }

        // Facturas (válidas y con errores de dominio)
        emitir(facturas, "F001", "01-09-2026", laura, pedro, List.of(laptop, impresora, soporte), 2300, "Efectivo");
        emitir(facturas, "F002", "15-08-2026", laura, juan, List.of(soporte), 200, "Tarjeta");
        emitir(facturas, "límite de crédito", "20-09-2026", marcos, juan, List.of(laptop), 0, "Transferencia");      // supera límite de crédito
        emitir(facturas, "pago excedido", "21-09-2026", laura, juan, List.of(soporte), 9999, "Efectivo");  // pago mayor al total
        emitir(facturas, "sin ítems", "22-09-2026", laura, juan, List.of(), 0, "Efectivo");            // sin ítems

        System.out.println("\n--- Facturas emitidas ---");
        for (Factura f : facturas.listar()) { pantalla.mostrarFactura(f); System.out.println(); }

        // Pago posterior sobre una factura con saldo pendiente
        Factura conSaldo = facturas.conSaldoPendiente().get(0);
        System.out.println("Saldo de " + conSaldo.getNumero() + " antes del pago: $" + conSaldo.getSaldo());
        try {
            facturas.registrarPago(conSaldo, 1_000_000);
        } catch (PagoExcedidoException ex) {
            System.out.println("[Pago rechazado] " + ex.getMessage());
        }
        try {
            facturas.registrarPago(conSaldo, conSaldo.getSaldo());
        } catch (PagoExcedidoException ex) {
            System.out.println("[Pago rechazado] " + ex.getMessage());
        }
        System.out.println("Estado: " + conSaldo.getPago().getEstado() + " | saldo: $" + conSaldo.getSaldo());

        // Orden natural (Comparable) y alternativo (Comparator)
        System.out.println("Facturas por monto (Comparable):");
        facturas.porMonto().forEach(f -> System.out.println("  " + f.getNumero() + " $" + f.calcularTotal()));
        System.out.println("Facturas por fecha (Comparator):");
        facturas.porFecha().forEach(f -> System.out.println("  " + f.getNumero() + " " + f.getFecha()));
        System.out.println("Clientes por cantidad de compras (Comparable):");
        clientes.porCompras().forEach(c -> System.out.println("  " + c.getNombre() + " " + c.getHistorialCompras().size()));
        System.out.println("Clientes por límite de crédito (Comparator):");
        clientes.porLimite().forEach(c -> System.out.println("  " + c.getNombre() + " $" + c.getLimiteCredito()));

        // Polimorfismo: clase abstracta Persona e interfaz Valorizable
        List<Persona> personas = List.of(juan, laura, laptop.getProveedor());
        personas.forEach(p -> System.out.println("\n" + p.resumen()));
        List<Valorizable> valorizables = new ArrayList<>(facturas.listar());
        valorizables.add(juan);
        valorizables.add(laptop);
        System.out.println("Valores (Valorizable):");
        valorizables.forEach(v -> System.out.println("  " + v.getClass().getSimpleName() + " -> $" + v.obtenerValor()));

        // Persistencia
        try {
            app.persistencia().guardarTodo();
            System.out.println("\nContenido leído de facturas.txt:");
            pantalla.listarRegistros(app.persistencia().leerFacturas());
            System.out.println("\nContenido leído de clientes.txt:");
            pantalla.listarRegistros(app.persistencia().leerClientes());
            ServicioPersistencia.Resultado r = app.persistencia().recuperarTodo();
            System.out.println("\nRecuperados desde archivo: " + r.clientes() + " clientes y " + r.facturas() + " facturas.");
            pantalla.resumenFacturas("Facturas recuperadas", facturas.listar());
            pantalla.listarClientes(clientes.listar());
        } catch (PersistenciaException e) {
            System.out.println("[Error de archivo] " + e.getMessage());
        }
    }

    private static void emitir(ServicioFacturacion servicio, String etiqueta, String fecha, Cliente c, Empleado e,
                               List<OfertaComercial> items, double monto, String forma) {
        try {
            servicio.emitir(fecha, c, e, items, monto, forma);
        } catch (FacturaSinItemsException | PagoExcedidoException | LimiteCreditoExcedidoException ex) {
            System.out.println("[Rechazada " + etiqueta + "] " + ex.getMessage());
        }
    }

    private static Cliente cliente(ServicioClientes s, String nombre) {
        return s.listar().stream().filter(c -> c.getNombre().equals(nombre)).findFirst().orElseThrow();
    }

    private static Empleado empleado(ServicioFacturacion s, String nombre) {
        return s.empleadosDisponibles().stream().filter(e -> e.getNombre().equals(nombre)).findFirst().orElseThrow();
    }

    private static OfertaComercial oferta(ServicioFacturacion s, String codigo) {
        return s.ofertasDisponibles().stream().filter(o -> o.getCodigo().equals(codigo)).findFirst().orElseThrow();
    }
}
