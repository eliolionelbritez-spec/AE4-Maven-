package com.example.dominio.gestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.dominio.catalogo.OfertaComercial;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.pagos.Factura;

/**
 * Raíz del modelo: reúne los datos del negocio (clientes, empleados, catálogo y facturas).
 * No conoce la consola ni los archivos; los casos de uso viven en la capa de servicio.
 */
public class Empresa {
    private final ArrayList<Cliente> clientes = new ArrayList<>();
    private final ArrayList<Empleado> empleados = new ArrayList<>();
    private final ArrayList<OfertaComercial> catalogo = new ArrayList<>();
    private final ArrayList<Factura> facturas = new ArrayList<>();

    public void agregarCliente(Cliente c) { clientes.add(c); }
    public void agregarEmpleado(Empleado e) { empleados.add(e); }
    public void agregarOferta(OfertaComercial o) { catalogo.add(o); }
    public void agregarFactura(Factura f) { facturas.add(f); }

    /** Reemplaza todos los clientes (al recuperar datos guardados). */
    public void reemplazarClientes(List<Cliente> nuevos) { clientes.clear(); clientes.addAll(nuevos); }
    /** Reemplaza todas las facturas (al recuperar datos guardados). */
    public void reemplazarFacturas(List<Factura> nuevas) { facturas.clear(); facturas.addAll(nuevas); }

    public List<Cliente> getClientes() { return Collections.unmodifiableList(clientes); }
    public List<Empleado> getEmpleados() { return Collections.unmodifiableList(empleados); }
    public List<OfertaComercial> getCatalogo() { return Collections.unmodifiableList(catalogo); }
    public List<Factura> getFacturas() { return Collections.unmodifiableList(facturas); }
}
