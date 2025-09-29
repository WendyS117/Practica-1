package uso;

import eddlineales.Colas;
import spp.ItemPedido;
import spp.Pedido;
import spp.Producto;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Clase para gestionar el procesamiento de pedidos (Ejercicio 2).
 * Se encarga de cargar pedidos a una cola, procesarlos (reduciendo stock)
 * y permitir la búsqueda de pedidos específicos.
 */
public class GestionPedidos {

    private Colas<Pedido> colaPedidosRecientes; // Cola para pedidos pendientes de procesar (FIFO)
    private GestionProductos gestionProductos; // Referencia al gestor de inventario para actualizar stock
    private GestionEnvios gestionEnvios; // Referencia al gestor de envíos para apilar pedidos procesados

    // Variables para el reporte consolidado
    private int totalPedidosProcesados = 0;
    private double valorTotalVendido = 0.0;

    /**
     * Constructor de GestionPedidos.
     * @param gestionProductos Instancia de GestionProductos para interactuar con el inventario.
     * @param gestionEnvios Instancia de GestionEnvios para pasarle los pedidos procesados.
     */
    public GestionPedidos(GestionProductos gestionProductos, GestionEnvios gestionEnvios) {
        this.colaPedidosRecientes = new Colas<>();
        this.gestionProductos = gestionProductos;
        this.gestionEnvios = gestionEnvios;
    }

    /**
     * Carga los pedidos desde el archivo "pedidos.txt" a la cola de pedidos recientes.
     * Cada línea del archivo representa un pedido con su cliente y sus ítems.
     */
    public void cargarPedidosIniciales() {
        int totalPedidosCargados = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("pedidos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                // El formato esperado es: Cliente ; SKU1 Cant1 | SKU2 Cant2 | ...
                String[] partes = linea.split(" ; ", 2); // Divide en cliente y la cadena de ítems
                if (partes.length < 2) {
                    System.err.println("Error: Formato de línea de pedido inválido en 'pedidos.txt': " + linea);
                    continue;
                }

                String cliente = partes[0].trim();
                String itemsStr = partes[1].trim();

                Pedido nuevoPedido = new Pedido(cliente); // Crea un nuevo objeto Pedido
                String[] itemsArray = itemsStr.split(" \\| "); // Divide la cadena de ítems por " | "

                for (String item : itemsArray) {
                    String[] itemPartes = item.trim().split(" "); // Divide cada ítem en SKU y Cantidad
                    if (itemPartes.length == 2) {
                        try {
                            String sku = itemPartes[0].trim();
                            int cantidad = Integer.parseInt(itemPartes[1].trim());
                            
                            // Para la carga inicial, creamos un Producto "dummy" con el SKU y cantidad.
                            // El precio y stock reales se manejarán en el inventario (GestionProductos)
                            // al momento de PROCESAR el pedido.
                            Producto dummyProducto = new Producto(sku, "Producto Desconocido", "N/A", 0.0, 0);
                            nuevoPedido.agregarItem(dummyProducto, cantidad);
                        } catch (NumberFormatException e) {
                            System.err.println("Error de formato numérico en cantidad de ítem: '" + item + "' en línea: " + linea);
                        }
                    } else {
                        System.err.println("Error: Formato de ítem de pedido inválido: '" + item + "' en línea: " + linea);
                    }
                }
                colaPedidosRecientes.enqueue(nuevoPedido); // Encola el pedido recién creado
                totalPedidosCargados++;
            }
            System.out.println("Se cargaron " + totalPedidosCargados + " pedidos iniciales a la cola de recientes.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo pedidos.txt: " + e.getMessage());
        }
    }

    /**
     * Muestra el menú para el procesamiento de pedidos (Ejercicio 2) y maneja las opciones.
     * @param scanner Objeto Scanner para la entrada del usuario.
     */
    public void menuProcesamientoPedidos(Scanner scanner) {
        int opcionPedidos;
        do {
            System.out.println("\n--- MENÚ PROCESAMIENTO DE PEDIDOS (EJERCICIO 2) ---");
            System.out.println("1. Procesar los últimos 6500 pedidos (FIFO)");
            System.out.println("2. Buscar un pedido específico por ID en la cola");
            System.out.println("3. Ver la cantidad de pedidos pendientes en la cola");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcionPedidos = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcionPedidos = -1;
            }

            switch (opcionPedidos) {
                case 1:
                    procesarPedidos(6500); // Procesa un número fijo de pedidos
                    break;
                case 2:
                    buscarPedidoEnCola(scanner); // Busca un pedido por ID
                    break;
                case 3:
                    System.out.println("Actualmente hay " + colaPedidosRecientes.sizeOf() + " pedidos pendientes en la cola.");
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal.");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcionPedidos != 0);
    }

    /**
     * Procesa un número determinado de pedidos de la cola de pedidos recientes.
     * Por cada pedido, intenta reducir el stock de los productos y actualiza su estado.
     * Los pedidos procesados se mueven a la pila de envíos.
     * @param cantidadAProcesar El número máximo de pedidos a intentar procesar.
     */
    private void procesarPedidos(int cantidadAProcesar) {
        int pedidosProcesadosExitosamenteSesion = 0;
        double valorVendidoSesion = 0.0;

        System.out.println("\nIniciando procesamiento de pedidos de la cola...");
        // Procesa pedidos mientras la cola no esté vacía y no se haya alcanzado la cantidad límite
        while (!colaPedidosRecientes.isEmpty() && pedidosProcesadosExitosamenteSesion < cantidadAProcesar) {
            Pedido pedidoActual = colaPedidosRecientes.dequeue(); // Desencola el primer pedido (FIFO)
            if (pedidoActual == null) continue; // Si por alguna razón el pedido es nulo, salta

            boolean stockSuficienteParaTodoElPedido = true;
            
            // Primero, intentamos reducir el stock para cada ítem del pedido
            // Si algún ítem no tiene stock suficiente, el pedido completo no se procesa
            Iterator<ItemPedido> itItems = pedidoActual.getItems().iterator();
            while(itItems.hasNext()) {
                ItemPedido item = itItems.next();
                // Intentamos actualizar el stock. Si falla (no hay suficiente stock), marcamos el pedido como no procesable
                if (!gestionProductos.actualizarStock(item.getProducto().getSku(), item.getCantidad())) {
                    stockSuficienteParaTodoElPedido = false;
                    System.out.println("  -> ERROR: Stock insuficiente para SKU " + item.getProducto().getSku() + " en pedido " + pedidoActual.getIdPedido() + ". Pedido no procesado.");
                    break; // Sale del bucle de ítems, ya que el pedido no se puede completar
                }
            }

            if (stockSuficienteParaTodoElPedido) {
                // Si todos los ítems tuvieron stock suficiente, el pedido se procesa
                pedidoActual.setEstado("PROCESADO"); // Cambia el estado del pedido
                pedidoActual.setFechaProcesamiento(new Date()); // Establece la fecha de procesamiento
                valorVendidoSesion += pedidoActual.getTotal(); // Suma al total vendido de la sesión
                pedidosProcesadosExitosamenteSesion++;
                gestionEnvios.apilarPedidoProcesado(pedidoActual); // Mueve el pedido procesado a la pila de envíos
                System.out.println("  -> Pedido " + pedidoActual.getIdPedido() + " procesado y movido a la pila de envíos.");
            } else {
                // Si no hubo stock suficiente, el pedido no se procesa y no se mueve a la pila de envíos.
                System.out.println("  -> Pedido " + pedidoActual.getIdPedido() + " no pudo ser procesado. Permanece pendiente o requiere revisión.");
            }
        }
        totalPedidosProcesados += pedidosProcesadosExitosamenteSesion; // Actualiza el total global
        valorTotalVendido += valorVendidoSesion; // Actualiza el valor global
        System.out.println("Se procesaron " + pedidosProcesadosExitosamenteSesion + " pedidos exitosamente en esta sesión.");
        System.out.printf("Valor total de ventas en esta sesión: $%.2f\n", valorVendidoSesion);
    }

    /**
     * Permite al usuario buscar un pedido por su ID en la cola de pedidos recientes.
     * No elimina el pedido de la cola.
     * @param scanner Objeto Scanner para la entrada del usuario.
     */
    private void buscarPedidoEnCola(Scanner scanner) {
        System.out.print("Ingrese el ID del pedido a buscar en la cola de recientes: ");
        String idBuscado = scanner.nextLine().trim();

        Pedido pedidoEncontrado = colaPedidosRecientes.buscar(idBuscado); // Usa el método 'buscar' de la clase Colas
        if (pedidoEncontrado != null) {
            System.out.println("\nPedido encontrado en la cola de recientes:");
            System.out.println(pedidoEncontrado.getInfo());
        } else {
            System.out.println("Pedido con ID '" + idBuscado + "' no encontrado en la cola de pedidos recientes.");
        }
    }

    // --- Getters para el reporte consolidado ---
    public int getTotalPedidosProcesados() {
        return totalPedidosProcesados;
    }

    public double getValorTotalVendido() {
        return valorTotalVendido;
    }

    public int getPedidosPendientesEnCola() {
        return colaPedidosRecientes.sizeOf();
    }
}