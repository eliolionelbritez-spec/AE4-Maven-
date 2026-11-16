package com.example.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.dominio.excepciones.DatoInvalidoException;
import com.example.dominio.gestion.Empresa;
import com.example.dominio.actores.Cliente;

/** Casos de uso sobre clientes: registrar y listar con distintos órdenes. */
public class ServicioClientes {
    private final Empresa empresa;

    public ServicioClientes(Empresa empresa) { this.empresa = empresa; }

    /** Crea y registra un cliente. Las validaciones las hace el dominio (DatoInvalidoException). */
    public Cliente registrar(String nombre, String domicilio, String dni, String telefono,
                             double limiteCredito, String categoria) {
        Cliente cliente = new Cliente(nombre, domicilio, dni, telefono, limiteCredito, categoria);
        for (Cliente existente : empresa.getClientes())
            if (existente.getDni().equals(cliente.getDni()))
                throw new DatoInvalidoException("Ya existe un cliente con DNI " + cliente.getDni());
        empresa.agregarCliente(cliente);
        return cliente;
    }

    public List<Cliente> listar() { return empresa.getClientes(); }

    /** Orden natural (Comparable): por cantidad de compras. */
    public List<Cliente> porCompras() {
        List<Cliente> l = new ArrayList<>(empresa.getClientes());
        Collections.sort(l);
        return l;
    }

    /** Orden alternativo (Comparator): por límite de crédito. */
    public List<Cliente> porLimite() {
        List<Cliente> l = new ArrayList<>(empresa.getClientes());
        Collections.sort(l, Cliente.POR_LIMITE_CREDITO);
        return l;
    }
}
