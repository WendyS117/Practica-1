package uso;

import eddlineales.Pilas;
import spp.Pedido;


public class GestionEnvios {

    private Pilas<Pedido> pilaPedidosParaEnvio;
    private int contEnvios = 0;

    /**
     * Constructor de la clase
     */
    public GestionEnvios() {
        this.pilaPedidosParaEnvio = new Pilas<>();
        this.contEnvios = 0;
    }

    /**
     * Apila un pedido que ha sido procesado y que este listo para ser procesado
     * @param pedido El pedido procesado a apilar
     */
    public void apilarPedidoProcesado(Pedido pedido) {
        pilaPedidosParaEnvio.push(pedido);
    }

    /**
     * Procesa los pedidos que estan en la pila
     * Cambia el estado del pedido 
     */
    public void procesarEnvios() {
        int enviosProcesados = 0;
        System.out.println("\nIniciando procesamiento de envios...");
        // Procesa pedidos mientras la pila no este vacia
        while (!pilaPedidosParaEnvio.isEmpty()) {
            Pedido pedidoAEnviar = pilaPedidosParaEnvio.pop(); // Desapila el último pedido procesado
            if (pedidoAEnviar == null) continue;

            pedidoAEnviar.setEstado("ENVIADO"); // Cambia el estado del pedido
            contEnvios++;
            enviosProcesados++;
            String guiaEnvio = pedidoAEnviar.getIdPedido() + "-" + enviosProcesados + "-G" + String.format("%06d", contEnvios);
            
            System.out.println("Pedido " + pedidoAEnviar.getIdPedido() + " enviado. Guia de envío generada: " + guiaEnvio);
        }
        System.out.println("Se procesaron " + enviosProcesados + " envios en esta sesion");
    }

    /**
     * Genera un reporte de los pedidos que todavia estan pendientes de ser procesados
     */
    public void generarReporteEnviosPendientes() {
        System.out.println("\n--- REPORTE DE ENVIOS PENDIENTES ---");
        System.out.println("Pedidos listos para ser enviados: " + pilaPedidosParaEnvio.size());
        if (pilaPedidosParaEnvio.isEmpty()) {
            System.out.println("En estos momentos no hay pedidos pendientes");
            return;
        }
    }

    public int getPedidosEnPilaParaEnvio() {
        return pilaPedidosParaEnvio.size();
    }
    
}
