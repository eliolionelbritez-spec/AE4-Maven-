# TP2 / AE4 - Paradigmas y Lenguajes de Programación II
Sistema de facturación en Java (proyecto Maven). Autor de la base: Elio Lionel Britez

## Ejecutar
- Menú: clase `com.example.presentacion.Main` (`mvn compile exec:java`, o desde el IDE).
- Recorrido automático: `com.example.presentacion.Demo` (incluye guardar → recuperar).
- Diagrama de clases actualizado: `docs/diagrama-clases.png` (fuente Graphviz: `docs/diagrama-clases.dot`).

## Menú: cómo cancelar
- En las listas de elección (cliente, empleado, factura) la opción **0 = Volver** cancela la operación.
- En cualquier pregunta de una operación (alta de cliente, nueva factura, registrar pago) se puede escribir **`volver`** para cancelar y regresar al menú principal sin cambiar nada.
- Una elección inválida o un monto que no es un número vuelve a preguntar (no expulsa de la operación). Una factura necesita al menos un ítem antes de continuar.
- Las opciones que solo listan o leen archivos no piden datos, así que no necesitan cancelación.

## Capas (`src/main/java/com/example`)
| Capa | Paquetes | Responsabilidad | Puede depender de |
|---|---|---|---|
| Presentación | `presentacion` | Entrada/salida por consola (`Menu`, `Pantalla`) y arranque (`Main`, `Aplicacion`) | servicio, dominio (y las excepciones de `persistencia` para informar errores). `Aplicacion` es la raíz de composición: la única que instancia las implementaciones concretas de persistencia |
| Servicio | `servicio` | Casos de uso: clientes, facturación, pagos, guardar/recuperar | dominio, interfaz `Repositorio` |
| Dominio | `dominio.*` (`actores`, `catalogo`, `descuentos`, `excepciones`, `gestion`, `interfaces`, `pagos`) | Entidades y reglas del negocio | solo el propio dominio |
| Persistencia | `persistencia` | Interfaz `Repositorio` + implementación en archivos de texto (guardar, recuperar y leer registros) | dominio |

## Problemas SOLID corregidos
| # | Problema | Principio | Solución |
|---|---|---|---|
| 1 | `Factura`/`Empresa` mezclaban cálculo, impresión por consola y formato de archivo | SRP | Capas: `Pantalla` imprime, los repositorios formatean |
| 2 | `Empresa`/`Menu` dependían de `GestorArchivo` concreto | DIP | Interfaz `Repositorio` |
| 3 | `Factura.emitir()` decidía el estado del pago con un ternario sobre *strings* | SRP | `EstadoPago` (enum) + `Pago.actualizarEstado()` |
| 4 | La categoría del cliente era un `String` validado con regex y sin comportamiento | OCP | `CategoriaCliente` + Strategy de descuento |
| 5 | La forma de pago era un texto libre sin validar ni comportamiento | OCP | Jerarquía `Pago` + Factory Method |
| 6 | El `switch` del menú crecía con cada opción | OCP | Lista de `Opcion(título, acción)` |
| 7 | `ServicioFacturacion` creaba con `new` la clase concreta `FormasDePago` | DIP | Se inyecta desde `Aplicacion` por constructor |

## Patrones de diseño
| Patrón | Categoría | Roles |
|---|---|---|
| Strategy | Comportamiento | `EstrategiaDescuento` (estrategia), `DescuentoRegular/Premium/Corporativo` (concretas), `Factura` (contexto) |
| Factory Method | Creación | `Pago` (producto), `PagoEfectivo/Transferencia/Tarjeta` (concretos), `FabricaPago` (creador), `FabricaPago*` (creadores concretos) |
| Adapter | Estructural | `Repositorio` (objetivo), `GestorArchivo` (adaptado), `RepositorioClientesArchivo/FacturasArchivo` (adaptadores) |

## Persistencia: guardar y recuperar
- **Guardar** (opción 10): `clientes.txt` y `facturas.txt`, una línea por registro, con todos los campos necesarios para reconstruir los objetos.
  - Cliente: `dni;nombre;domicilio;telefono;categoria;limite;saldo;compra1|compra2|...`
  - Factura: `numero;fecha;dniCliente;dniEmpleado;total;formaPago;numeroPago;montoPagado;estado;codItem1|codItem2|...`
- **Recuperar** (opción 11): `ServicioPersistencia.recuperarTodo()` carga primero los clientes y después las facturas (que referencian a clientes, empleados e ítems por DNI/código) y reemplaza los datos en memoria. El pago se recrea con la fábrica de su forma de pago. Si algún archivo falta o está dañado, la empresa queda como estaba.
- **Leer archivo** (opciones 12 y 13): muestra las líneas tal como están guardadas.
- Empleados y catálogo no se persisten: salen de `CargaInicial`. El DNI de cada cliente es único (se valida al registrar), porque es la clave con la que se enlazan las facturas.
