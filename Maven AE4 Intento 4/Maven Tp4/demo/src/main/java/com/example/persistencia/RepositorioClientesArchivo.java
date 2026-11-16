package com.example.persistencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.dominio.actores.Cliente;
import com.example.dominio.excepciones.DatoInvalidoException;

/**
 * Guarda y recupera clientes en un archivo de texto. El formato de línea es un detalle de esta capa:
 * dni;nombre;domicilio;telefono;categoria;limite;saldo;compra1|compra2|...
 */
public class RepositorioClientesArchivo implements Repositorio<Cliente> {
    private static final int CAMPOS = 8;
    private final GestorArchivo archivo;

    public RepositorioClientesArchivo(String ruta) { this.archivo = new GestorArchivo(ruta); }

    @Override
    public void guardar(List<? extends Cliente> clientes) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) lineas.add(aLinea(c));
        archivo.escribir(lineas);
    }

    @Override
    public List<Cliente> cargar() throws PersistenciaException {
        List<Cliente> clientes = new ArrayList<>();
        int n = 0;
        for (String linea : archivo.leer()) {
            n++;
            if (linea.isBlank()) continue;
            clientes.add(desdeLinea(linea, n));
        }
        return clientes;
    }

    @Override
    public List<String> leerRegistros() throws PersistenciaException { return archivo.leer(); }

    private String aLinea(Cliente c) {
        List<String> compras = new ArrayList<>();
        for (String compra : c.getHistorialCompras()) compras.add(Formato.limpiar(compra));
        return String.join(Formato.SEP, c.getDni(), Formato.limpiar(c.getNombre()),
                Formato.limpiar(c.getDomicilio()), Formato.limpiar(c.getTelefono()),
                c.getCategoria().toString(), String.valueOf(c.getLimiteCredito()),
                String.valueOf(c.getSaldoPendiente()), String.join(Formato.SEP_LISTA, compras));
    }

    private Cliente desdeLinea(String linea, int numero) throws PersistenciaException {
        String[] c = linea.split(Formato.SEP, -1);
        if (c.length != CAMPOS)
            throw new PersistenciaException("Clientes, línea " + numero + ": formato inválido (" + linea + ")");
        try {
            Cliente cliente = new Cliente(c[1], c[2], c[0], c[3], Double.parseDouble(c[5]), c[4]);
            List<String> compras = c[7].isEmpty() ? List.of() : Arrays.asList(c[7].split("\\" + Formato.SEP_LISTA));
            cliente.restaurarEstado(Double.parseDouble(c[6]), compras);
            return cliente;
        } catch (NumberFormatException | DatoInvalidoException e) {
            throw new PersistenciaException("Clientes, línea " + numero + ": " + e.getMessage(), e);
        }
    }
}
