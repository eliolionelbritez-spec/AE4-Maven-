package com.example.servicio;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.gestion.Empresa;
import com.example.dominio.actores.Cliente;
import com.example.dominio.pagos.Factura;
import com.example.persistencia.PersistenciaException;
import com.example.persistencia.Repositorio;

/** Guardar y recuperar datos. Depende de la interfaz Repositorio, no de una tecnología concreta. */
public class ServicioPersistencia {
    /** Cantidad de registros recuperados. */
    public record Resultado(int clientes, int facturas) { }

    private final Empresa empresa;
    private final Repositorio<Cliente> repoClientes;
    private final Repositorio<Factura> repoFacturas;

    public ServicioPersistencia(Empresa empresa, Repositorio<Cliente> repoClientes,
                                Repositorio<Factura> repoFacturas) {
        this.empresa = empresa;
        this.repoClientes = repoClientes;
        this.repoFacturas = repoFacturas;
    }

    public void guardarFacturas() throws PersistenciaException { repoFacturas.guardar(empresa.getFacturas()); }
    public void guardarClientes() throws PersistenciaException { repoClientes.guardar(empresa.getClientes()); }

    public void guardarTodo() throws PersistenciaException {
        guardarFacturas();
        guardarClientes();
    }

    /**
     * Recupera clientes y facturas guardados y reemplaza los que hay en memoria. Primero los clientes
     * (las facturas los referencian). Si algo falla, la empresa queda como estaba.
     */
    public Resultado recuperarTodo() throws PersistenciaException {
        repoClientes.leerRegistros();   // falla antes de tocar nada si algún archivo no existe
        repoFacturas.leerRegistros();
        List<Cliente> clientesPrevios = new ArrayList<>(empresa.getClientes());
        List<Factura> facturasPrevias = new ArrayList<>(empresa.getFacturas());
        try {
            empresa.reemplazarClientes(repoClientes.cargar());
            empresa.reemplazarFacturas(repoFacturas.cargar());
        } catch (PersistenciaException e) {
            empresa.reemplazarClientes(clientesPrevios);
            empresa.reemplazarFacturas(facturasPrevias);
            throw e;
        }
        return new Resultado(empresa.getClientes().size(), empresa.getFacturas().size());
    }

    public List<String> leerFacturas() throws PersistenciaException { return repoFacturas.leerRegistros(); }
    public List<String> leerClientes() throws PersistenciaException { return repoClientes.leerRegistros(); }
}
