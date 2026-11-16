package com.example.presentacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.excepciones.FacturaSinItemsException;
import com.example.dominio.excepciones.LimiteCreditoExcedidoException;
import com.example.dominio.excepciones.PagoExcedidoException;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.pagos.Factura;
import com.example.persistencia.DatosInexistentesException;
import com.example.persistencia.PersistenciaException;
import com.example.servicio.ServicioClientes;
import com.example.servicio.ServicioFacturacion;
import com.example.servicio.ServicioPersistencia;

/**
 * Menú de consola: pide datos y delega cada opción en un servicio. No contiene lógica de negocio.
 * Las opciones son una lista de (título, acción): agregar una opción no obliga a tocar el ciclo del menú.
 */
public class Menu {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Palabra que, escrita en cualquier pregunta, cancela la operación en curso y vuelve al menú. */
    private static final String VOLVER = "volver";

    /** Señal interna: se canceló la operación (palabra "volver", opción 0 o fin de la entrada). */
    private static final class Cancelado extends RuntimeException {
        private static final long serialVersionUID = 1L;
        Cancelado() { super("operación cancelada", null, false, false); }
    }

    @FunctionalInterface
    private interface Accion { void ejecutar() throws PersistenciaException; }

    private record Opcion(String titulo, Accion accion) { }

    private final ServicioClientes clientes;
    private final ServicioFacturacion facturas;
    private final ServicioPersistencia persistencia;
    private final Pantalla pantalla = new Pantalla();
    private final Scanner in = new Scanner(System.in);
    private final List<Opcion> opciones = new ArrayList<>();

    public Menu(ServicioClientes clientes, ServicioFacturacion facturas, ServicioPersistencia persistencia) {
        this.clientes = clientes;
        this.facturas = facturas;
        this.persistencia = persistencia;
        registrarOpciones();
    }

    private void registrarOpciones() {
        opciones.add(new Opcion("Registrar cliente", this::registrarCliente));
        opciones.add(new Opcion("Listar clientes", () -> pantalla.listarClientes(clientes.listar())));
        opciones.add(new Opcion("Nueva factura", this::nuevaFactura));
        opciones.add(new Opcion("Registrar pago de una factura", this::registrarPago));
        opciones.add(new Opcion("Listar facturas (detalle)",
                () -> facturas.listar().forEach(f -> { pantalla.mostrarFactura(f); System.out.println(); })));
        opciones.add(new Opcion("Facturas por monto (Comparable)",
                () -> pantalla.resumenFacturas("Facturas ordenadas por monto", facturas.porMonto())));
        opciones.add(new Opcion("Facturas por fecha (Comparator)",
                () -> pantalla.resumenFacturas("Facturas ordenadas por fecha", facturas.porFecha())));
        opciones.add(new Opcion("Clientes por cantidad de compras (Comparable)",
                () -> pantalla.listarClientes(clientes.porCompras())));
        opciones.add(new Opcion("Clientes por límite de crédito (Comparator)",
                () -> pantalla.listarClientes(clientes.porLimite())));
        opciones.add(new Opcion("Guardar facturas y clientes en archivos", () -> {
            persistencia.guardarTodo();
            System.out.println("Facturas y clientes guardados en sus archivos.");
        }));
        opciones.add(new Opcion("Recuperar facturas y clientes desde archivos", () -> {
            ServicioPersistencia.Resultado r = persistencia.recuperarTodo();
            System.out.println("Recuperados " + r.clientes() + " clientes y " + r.facturas() + " facturas.");
        }));
        opciones.add(new Opcion("Leer archivo de facturas", () -> pantalla.listarRegistros(persistencia.leerFacturas())));
        opciones.add(new Opcion("Leer archivo de clientes", () -> pantalla.listarRegistros(persistencia.leerClientes())));
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarOpciones();
            opcion = leerOpcionPrincipal();
            if (opcion == 0) System.out.println("Hasta luego.");
            else ejecutar(opcion);
        } while (opcion != 0);
    }

    private void ejecutar(int opcion) {
        if (opcion < 1 || opcion > opciones.size()) {
            System.out.println("Opción inválida.");
            return;
        }
        try {
            opciones.get(opcion - 1).accion().ejecutar();
        } catch (Cancelado e) {
            System.out.println("Operación cancelada. Volviendo al menú.");
        } catch (DatoInvalidoException e) {
            System.out.println("[Dato inválido] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Se esperaba un número.");
        } catch (DatosInexistentesException e) {
            System.out.println("[Archivo inexistente] Todavía no se guardó " + e.getOrigen() + ". Usá primero la opción 10 (guardar).");
        } catch (PersistenciaException e) {
            System.out.println("[Error de archivo] " + e.getMessage());
        }
    }

    private void mostrarOpciones() {
        System.out.println("\n===== MENÚ =====");
        for (int i = 0; i < opciones.size(); i++)
            System.out.printf("%2d) %s%n", i + 1, opciones.get(i).titulo());
        System.out.println(" 0) Salir");
    }

    private void registrarCliente() {
        avisoCancelar();
        String nombre = leer("Nombre: ");
        String domicilio = leer("Domicilio: ");
        String dni = leer("DNI (7 u 8 dígitos): ");
        String telefono = leer("Teléfono: ");
        double limite = leerDouble("Límite de crédito: ");
        String categoria = leer("Categoría (regular/premium/corporativo): ");
        clientes.registrar(nombre, domicilio, dni, telefono, limite, categoria);
        System.out.println("Cliente registrado.");
    }

    private void nuevaFactura() {
        avisoCancelar();
        Cliente cliente = elegir("cliente", clientes.listar(), c -> c.getNombre() + " (" + c.getCategoria() + ")");
        if (cliente == null) return;
        Empleado empleado = elegir("empleado", facturas.empleadosDisponibles(), e -> e.getNombre() + " - " + e.getPuesto());
        if (empleado == null) return;

        List<OfertaComercial> catalogo = facturas.ofertasDisponibles();
        for (int i = 0; i < catalogo.size(); i++)
            System.out.println((i + 1) + ") " + catalogo.get(i).resumen() + " - $" + catalogo.get(i).calcularPrecioFinal());
        List<OfertaComercial> items = new ArrayList<>();
        while (true) {
            int n = leerInt("Agregar ítem (número; 0 = terminar): ");
            if (n == 0) {
                if (!items.isEmpty()) break;
                System.out.println("Agregá al menos un ítem (o escribí 'volver' para cancelar).");
            } else if (n >= 1 && n <= catalogo.size()) {
                items.add(catalogo.get(n - 1));
                System.out.println("  + " + catalogo.get(n - 1).resumen());
            } else {
                System.out.println("Ítem inexistente.");
            }
        }

        String fecha = leer("Fecha dd-MM-yyyy (Enter = hoy): ");
        if (fecha.isEmpty()) fecha = LocalDate.now().format(FORMATO);
        double monto = leerDouble("Monto que paga ahora: ");
        String forma = leer("Forma de pago (" + String.join("/", facturas.formasDePagoDisponibles()) + "): ");

        try {
            Factura f = facturas.emitir(fecha, cliente, empleado, items, monto, forma);
            System.out.println("\nFactura emitida:");
            pantalla.mostrarFactura(f);
        } catch (FacturaSinItemsException | PagoExcedidoException | LimiteCreditoExcedidoException e) {
            System.out.println("[No se pudo emitir] " + e.getMessage());
        }
    }

    private void registrarPago() {
        List<Factura> pendientes = facturas.conSaldoPendiente();
        if (pendientes.isEmpty()) { System.out.println("No hay facturas con saldo pendiente."); return; }
        avisoCancelar();
        Factura factura = elegir("factura", pendientes,
                f -> f.getNumero() + " | " + f.getCliente().getNombre() + " | total $" + f.calcularTotal()
                        + " | saldo $" + f.getSaldo());
        if (factura == null) return;
        double importe = leerDouble("Monto a pagar: ");
        try {
            facturas.registrarPago(factura, importe);
            System.out.println("Pago registrado. Estado: " + factura.getPago().getEstado() + " | Saldo: $" + factura.getSaldo());
        } catch (PagoExcedidoException e) {
            System.out.println("[No se pudo registrar] " + e.getMessage());
        }
    }

    /** Muestra la lista numerada y pide una elección. Repite hasta que sea válida; 0 cancela la operación. */
    private <T> T elegir(String titulo, List<T> lista, Function<T, String> descripcion) {
        if (lista.isEmpty()) { System.out.println("No hay " + titulo + "s cargados."); return null; }
        for (int i = 0; i < lista.size(); i++) System.out.println((i + 1) + ") " + descripcion.apply(lista.get(i)));
        System.out.println("0) Volver");
        while (true) {
            int n = leerInt("Elegí " + titulo + " (0 = volver): ");
            if (n == 0) throw new Cancelado();
            if (n >= 1 && n <= lista.size()) return lista.get(n - 1);
            System.out.println("Selección inválida.");
        }
    }

    private void avisoCancelar() {
        System.out.println("(Escribí '" + VOLVER + "' en cualquier pregunta para cancelar y volver al menú)");
    }

    /** Lee una línea sin interpretarla. Devuelve null si ya no hay más entrada. */
    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    /** Menú principal: no se puede "volver" desde acá; sin más entrada se cierra el programa. */
    private int leerOpcionPrincipal() {
        String texto = leerLinea("Opción: ");
        if (texto == null) return 0;
        try { return Integer.parseInt(texto); }
        catch (NumberFormatException e) { return -1; }
    }

    /** Pregunta dentro de una operación: escribir "volver" (o quedarse sin entrada) la cancela. */
    private String leer(String mensaje) {
        String texto = leerLinea(mensaje);
        if (texto == null || texto.equalsIgnoreCase(VOLVER)) throw new Cancelado();
        return texto;
    }

    private int leerInt(String mensaje) {
        try { return Integer.parseInt(leer(mensaje)); }
        catch (NumberFormatException e) { return -1; }
    }

    /** Repite la pregunta hasta recibir un número (acepta coma o punto decimal). */
    private double leerDouble(String mensaje) {
        while (true) {
            try { return Double.parseDouble(leer(mensaje).replace(',', '.')); }
            catch (NumberFormatException e) { System.out.println("Ingresá un número (o escribí '" + VOLVER + "')."); }
        }
    }
}
