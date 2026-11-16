package com.example.persistencia;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.excepciones.FacturaSinItemsException;
import com.example.dominio.excepciones.PagoExcedidoException;
import com.example.dominio.gestion.Empresa;
import com.example.dominio.pagos.Factura;
import com.example.dominio.pagos.FormasDePago;
import com.example.dominio.pagos.Pago;

/**
 * Guarda y recupera facturas en un archivo de texto. El formato de línea es un detalle de esta capa:
 * numero;fecha;dniCliente;dniEmpleado;total;formaPago;numeroPago;montoPagado;estado;codItem1|codItem2|...
 * Al recuperar, cliente, empleado e ítems se buscan en la empresa (por DNI y por código) y el pago
 * se recrea con la fábrica de su forma de pago.
 */
public class RepositorioFacturasArchivo implements Repositorio<Factura> {
    private static final int CAMPOS = 10;
    private final GestorArchivo archivo;
    private final Empresa empresa;
    private final FormasDePago formasDePago;

    public RepositorioFacturasArchivo(String ruta, Empresa empresa, FormasDePago formasDePago) {
        this.archivo = new GestorArchivo(ruta);
        this.empresa = empresa;
        this.formasDePago = formasDePago;
    }

    @Override
    public void guardar(List<? extends Factura> facturas) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (Factura f : facturas) lineas.add(aLinea(f));
        archivo.escribir(lineas);
    }

    @Override
    public List<Factura> cargar() throws PersistenciaException {
        List<Factura> facturas = new ArrayList<>();
        int n = 0;
        for (String linea : archivo.leer()) {
            n++;
            if (linea.isBlank()) continue;
            facturas.add(desdeLinea(linea, n));
        }
        return facturas;
    }

    @Override
    public List<String> leerRegistros() throws PersistenciaException { return archivo.leer(); }

    private String aLinea(Factura f) {
        List<String> codigos = new ArrayList<>();
        for (OfertaComercial i : f.getItems()) codigos.add(Formato.limpiar(i.getCodigo()));
        return String.join(Formato.SEP, f.getNumero(), f.getFecha(), f.getCliente().getDni(),
                f.getEmpleado().getDni(), String.valueOf(f.calcularTotal()), f.getPago().getFormaPago(),
                f.getPago().getNumero(), String.valueOf(f.getPago().getMonto()),
                f.getPago().getEstado().toString(), String.join(Formato.SEP_LISTA, codigos));
    }

    private Factura desdeLinea(String linea, int numero) throws PersistenciaException {
        String[] c = linea.split(Formato.SEP, -1);
        if (c.length != CAMPOS)
            throw new PersistenciaException("Facturas, línea " + numero + ": formato inválido (" + linea + ")");
        try {
            Cliente cliente = buscarCliente(c[2], numero);
            Empleado empleado = buscarEmpleado(c[3], numero);
            ArrayList<OfertaComercial> items = new ArrayList<>();
            for (String codigo : c[9].split("\\" + Formato.SEP_LISTA)) items.add(buscarOferta(codigo, numero));
            Pago pago = formasDePago.fabricaPara(c[5]).crearPago(c[6], c[1], Double.parseDouble(c[7]));
            Factura factura = new Factura(c[0], c[1], cliente, empleado, items, pago);
            // No se llama a emitir(): la deuda del cliente ya viene restaurada desde el archivo de clientes.
            pago.actualizarEstado(factura.calcularTotal());
            return factura;
        } catch (NumberFormatException | DatoInvalidoException | FacturaSinItemsException | PagoExcedidoException e) {
            throw new PersistenciaException("Facturas, línea " + numero + ": " + e.getMessage(), e);
        }
    }

    private Cliente buscarCliente(String dni, int numero) throws PersistenciaException {
        for (Cliente c : empresa.getClientes()) if (c.getDni().equals(dni)) return c;
        throw new PersistenciaException("Facturas, línea " + numero + ": no existe el cliente con DNI " + dni);
    }

    private Empleado buscarEmpleado(String dni, int numero) throws PersistenciaException {
        for (Empleado e : empresa.getEmpleados()) if (e.getDni().equals(dni)) return e;
        throw new PersistenciaException("Facturas, línea " + numero + ": no existe el empleado con DNI " + dni);
    }

    private OfertaComercial buscarOferta(String codigo, int numero) throws PersistenciaException {
        for (OfertaComercial o : empresa.getCatalogo()) if (o.getCodigo().equals(codigo)) return o;
        throw new PersistenciaException("Facturas, línea " + numero + ": no existe el ítem " + codigo);
    }
}
