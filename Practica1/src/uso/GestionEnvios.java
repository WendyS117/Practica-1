package uso;

import eddlineales.Pilas;
import spp.Pedido;
import java.util.Scanner;

/**
 * Clase para gestionar el sistema de envíos (Ejercicio 3).
 * Utiliza una pila para los pedidos que ya han sido procesados y están listos para ser enviados.
 */
public class GestionEnvios {

    private Pilas<Pedido> pilaPedidosParaEnvio; // Pila para pedidos ya procesados, listos para envío (LIFO)

    /**
     * Constructor de GestionEnvios.
     */
    public GestionEnvios() {
        this.pilaPedidosParaEnvio = new Pilas<>();
    }

    /**
     * Apila un pedido que ha sido procesado y está listo para ser enviado.
     * @param pedido El pedido procesado a apilar.
     */
    public void apilarPedidoProcesado(Pedido pedido) {
        pilaPedidosParaEnvio.push(pedido);
    }

    /**
     * Muestra el menú para el sistema de envíos (Ejercicio 3) y maneja las opciones.
     * @param scanner Objeto Scanner para la entrada del usuario.
     */
    public void menuSistemaEnvios(Scanner scanner) {
        int opcionEnvios;
        do {
            System.out.println("\n--- MENÚ SISTEMA DE ENVÍOS (EJERCICIO 3) ---");
            System.out.println("1. Procesar envíos pendientes (desapilar y generar guía)");
            System.out.println("2. Generar reporte de envíos pendientes en la pila");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcionEnvios = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcionEnvios = -1;
            }

            switch (opcionEnvios) {
                case 1:
                    procesarEnvios(); // Procesa los pedidos de la pila de envíos
                    break;
                case 2:
                    generarReporteEnviosPendientes(); // Genera un reporte de la pila de envíos
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal.");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcionEnvios != 0);
    }

    /**
     * Procesa los pedidos que están en la pila de envíos (LIFO).
     * Cambia el estado del pedido a "ENVIADO" y genera una guía de envío simulada.
     */
    private void procesarEnvios() {
        int enviosProcesadosSesion = 0;
        System.out.println("\nIniciando procesamiento de envíos desde la pila...");
        // Procesa pedidos mientras la pila no esté vacía
        while (!pilaPedidosParaEnvio.isEmpty()) {
            Pedido pedidoAEnviar = pilaPedidosParaEnvio.pop(); // Desapila el último pedido procesado (LIFO)
            if (pedidoAEnviar == null) continue;

            pedidoAEnviar.setEstado("ENVIADO"); // Cambia el estado del pedido a ENVIADO
            enviosProcesadosSesion++;
            // Generar guía de envío: ID del pedido - número de procesamiento - 'G' y número de procesamiento
            String guiaEnvio = pedidoAEnviar.getIdPedido() + "-" + enviosProcesadosSesion + "-G" + String.format("%06d", enviosProcesadosSesion);
            
            System.out.println("  -> Pedido " + pedidoAEnviar.getIdPedido() + " enviado. Guía de envío generada: " + guiaEnvio);
        }
        System.out.println("Se procesaron " + enviosProcesadosSesion + " envíos en esta sesión.");
    }

    /**
     * Genera un reporte de los pedidos que aún están en la pila de envíos (pendientes de ser procesados para envío).
     */
    private void generarReporteEnviosPendientes() {
        System.out.println("\n--- REPORTE DE ENVÍOS PENDIENTES ---");
        System.out.println("Pedidos actualmente en la pila de envío (listos para ser enviados): " + pilaPedidosParaEnvio.size());
        if (pilaPedidosParaEnvio.isEmpty()) {
            System.out.println("No hay pedidos pendientes de envío en este momento.");
        } else {
            // Para mostrar los detalles de los pedidos en la pila sin desapilarlos,
            // necesitaríamos un método de iteración en la clase Pilas.java.
            // Como no podemos modificar Pilas.java para añadir un iterador,
            // solo podemos reportar el tamaño.
            System.out.println("Para ver los detalles de cada pedido pendiente de envío, se necesitaría un método de recorrido en la clase Pilas.");
            System.out.println("Por ahora, solo se muestra el total de pedidos en la pila.");
        }
    }

    // --- Getter para el reporte consolidado ---
    public int getPedidosEnPilaParaEnvio() {
        return pilaPedidosParaEnvio.size();
    }
}
