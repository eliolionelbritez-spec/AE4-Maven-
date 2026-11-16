package com.example.servicio;

import com.example.dominio.catalogo.Producto;
import com.example.dominio.catalogo.Servicio;
import com.example.dominio.gestion.Departamento;
import com.example.dominio.gestion.Empresa;
import com.example.dominio.actores.Cliente;
import com.example.dominio.actores.Empleado;
import com.example.dominio.actores.Proveedor;

/** Datos de ejemplo con los que arranca el sistema (los mismos que armaba antes el Main). */
public final class CargaInicial {
    private CargaInicial() { }

    public static void cargar(Empresa empresa) {
        Departamento departamento = new Departamento("Ventas", 100000, null);
        Empleado juan = new Empleado("Juan", "Domicilio", "12345678", "1234567890",
                5000, "Administrativo", "01-01-2023", departamento);
        departamento.setResponsable(juan);
        Empleado pedro = new Empleado("Pedro", "Domicilio", "23456789", "0987654321",
                4000, "Técnico", "02-01-2023", departamento);

        Cliente laura = new Cliente("Laura", "Domicilio", "34567890", "5555555555", 10000, "Regular");
        laura.agregarHistorialCompra("Compra de laptop");
        Cliente marcos = new Cliente("Marcos", "Domicilio", "45678901", "4444444444", 1000, "Premium");

        Proveedor proveedor = new Proveedor("Proveedor X", "Domicilio", "11122233", "1111111111",
                "Proveedor de Productos", "X123");
        Producto laptop = new Producto("P001", "Laptop", 1500, "Electrónica", proveedor);
        Producto impresora = new Producto("P002", "Impresora", 300, "Electrónica", proveedor);
        Servicio soporte = new Servicio("S001", "Servicio Técnico", 500, "Servicio", proveedor);
        proveedor.agregarProductoSuministrado(laptop);
        proveedor.agregarProductoSuministrado(impresora);

        empresa.agregarEmpleado(juan);
        empresa.agregarEmpleado(pedro);
        empresa.agregarCliente(laura);
        empresa.agregarCliente(marcos);
        empresa.agregarOferta(laptop);
        empresa.agregarOferta(impresora);
        empresa.agregarOferta(soporte);
    }
}
