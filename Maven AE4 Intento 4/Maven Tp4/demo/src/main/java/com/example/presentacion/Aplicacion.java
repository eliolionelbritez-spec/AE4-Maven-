package com.example.presentacion;

import com.example.dominio.gestion.Empresa;
import com.example.dominio.pagos.FormasDePago;
import com.example.persistencia.RepositorioClientesArchivo;
import com.example.persistencia.RepositorioFacturasArchivo;
import com.example.servicio.CargaInicial;
import com.example.servicio.ServicioClientes;
import com.example.servicio.ServicioFacturacion;
import com.example.servicio.ServicioPersistencia;

/**
 * Punto de ensamblado (raíz de composición): es el único lugar que conoce todas las capas y decide
 * qué implementación concreta usa cada una (por ejemplo, persistir en archivos de texto).
 */
public class Aplicacion {
    private final ServicioClientes clientes;
    private final ServicioFacturacion facturas;
    private final ServicioPersistencia persistencia;

    public Aplicacion() {
        Empresa empresa = new Empresa();
        CargaInicial.cargar(empresa);
        FormasDePago formasDePago = new FormasDePago();
        this.clientes = new ServicioClientes(empresa);
        this.facturas = new ServicioFacturacion(empresa, formasDePago);
        this.persistencia = new ServicioPersistencia(empresa,
                new RepositorioClientesArchivo("clientes.txt"),
                new RepositorioFacturasArchivo("facturas.txt", empresa, formasDePago));
    }

    public ServicioClientes clientes() { return clientes; }
    public ServicioFacturacion facturas() { return facturas; }
    public ServicioPersistencia persistencia() { return persistencia; }

    public void iniciar() { new Menu(clientes, facturas, persistencia).iniciar(); }
}
