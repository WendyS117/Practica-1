package uso;

import eddlineales.*;
import spp.*;
import java.io.*;
import java.util.*;

public class GestionPedidos {

    private Colas<Pedido> pedidosRecientes;
    private GestionProductos gestionProductos;
    private GestionEnvios gestionEnvios;
    private int totalPedidosProce = 0;
    private double valorTotalVendido = 0.0;
    
    public GestionPedidos() {
    	this.pedidosRecientes = new Colas<>();
    	this.gestionProductos = null;
    	this.gestionEnvios = null;
    }
    
	public GestionPedidos(GestionProductos gestionProductos, GestionEnvios gestionEnvios) {
		this.pedidosRecientes = new Colas<>();
		this.gestionProductos = gestionProductos;
		this.gestionEnvios = gestionEnvios;
	}
    
	/**
	 * metodo para cargar los pedidos desde el archivo "pedidos.txt" a la cola de pedidos recientes
	 * Cada linea del archivo representa un pedido con su cliente y sus items
	 */
    public void cargarPedidosIniciales() {
    	int totalPedidosCargados = 0;
    	try(BufferedReader br = new BufferedReader(new FileReader("pedidos.txt"))){
    		String linea;
    		while((linea = br.readLine()) != null) {
    			linea = linea.trim();
    			if(linea.isEmpty()) continue;
    			
    			String[] partes = linea.split(" ; ", -1);//divide en cliente y la cadena de items
    			if(partes.length < 4) {
    				System.err.println("Error. Formato de linea de pedido invalido en pedidos.txt");
    				continue;
    			}
    			
    			String idPedido = partes[0].trim();
    			String cliente = partes[1].trim();
    			String estado = partes[2].trim();
                String itemsStr = partes[3].trim();
                
                Pedido nuevoPedido = new Pedido(cliente); //crea un nuevo pedido
                nuevoPedido.setIdPedido(idPedido);
                nuevoPedido.setEstado(estado);
                String[] itemsArray = itemsStr.split("\\s*\\|\\s*"); //divide la cadena de items por " | "
                for (String item : itemsArray) {
                	item = item.trim();
                	if(item.isEmpty())
                		continue;
                	String[] itemPartes = item.split("\\s+");
                	if (itemPartes.length == 2) {
                		try {
                			String sku = itemPartes[0].trim();
                            int cantidad = Integer.parseInt(itemPartes[1].trim());
                            Producto dProd = new Producto(sku, "Producto Desconocido", "N/A", 0.0, 0);
                            nuevoPedido.agregarItem(dProd, cantidad);
                		}catch (NumberFormatException e) {
                			System.err.println("Error de formato numerico en cantidad de item: '" + item + "' en linea: " + linea);
						}
                	}else {
                		System.err.println("Error: Formato de Item de pedido invalido (se esperan 'SKU cantidad'): '" + item + "' en linea: " + linea);
                	}

                }
                pedidosRecientes.enqueue(nuevoPedido); //encola el pedido recien creado
                totalPedidosCargados++;
    		}
    		System.out.println("Se cargaron " + totalPedidosCargados + " pedidos iniciales a la cola de recientes");
    	}catch (IOException e) {
			System.err.println("Error al leer el archivo pedidos.txt" + e.getMessage());
		}
    }
    
    /**
     * metodo que procesa un numero determinado de pedidos de la cola de pedidos recientes
     * Por cada pedido reduce el stock de los productos y actualiza el estado 
     * Los pedidos procesados se mueven a la pila de envios
     * @param cantidadAProcesar el numero maximo de pedidos que se van a procesar
     */
    public void procesarPedidos(int cantidadAProcesar) {
    	int pedidosProcesadosE = 0;
    	double valorVendido = 0.0;
    	
    	System.out.println("\nIniciando procesamiento de pedidos de la cola...");
    	while(!pedidosRecientes.isEmpty() && pedidosProcesadosE < cantidadAProcesar) {
    		Pedido actual = pedidosRecientes.dequeue(); //desencola el primer pedido
    		if(actual == null) continue;
    		
    		boolean stockSuficiente = true;
    		
    		Iterator<ItemPedido> itItems = actual.getItems().iterator();
    		while(itItems.hasNext()) {
    			ItemPedido item = itItems.next();
    			if(!gestionProductos.actualizarStock(item.getProducto().getSku(), item.getCantidad())) {
    				stockSuficiente = false;
    				System.out.println("Error: stock insuficiente para SKU" + item.getProducto().getSku() + " en pedido " + actual.getIdPedido() + ". Pedido no procesado");
    				break; //sale del bucle de items, ya que el pedido no se puede completar
    			}
    		}
    		if(stockSuficiente) {
    			//Si todos los items tuvieron sotck suficiente el pedido se procesa
    			actual.setEstado("Se ha procesado");
    			actual.setFechaProcesamiento(new Date());
    			valorVendido += actual.getTotal();
    			pedidosProcesadosE++;
    			gestionEnvios.apilarPedidoProcesado(actual);
    			System.out.println("Pedido " + actual.getIdPedido() + " procesado y se ha movido a la pila de envios");
    		}else {
    			System.out.println("Pedido " + actual.getIdPedido() + " no se pudo procesar");
    		}
    	}
    	totalPedidosProce += pedidosProcesadosE;
    	valorTotalVendido += valorVendido;
    	System.out.println("Se procesaron " + pedidosProcesadosE + " pedidos exitosamente en esta sesion");
    	System.out.printf("Valor total de ventas en esta sesion: $%.2f\n", valorVendido);
    }
    
    /**
     * Busca un pedido por su ID en la cola de pedidos recientes
     * No elimina el pedido
     * @param scanner para la entrada del usuario
     */
    public void buscarPedido(String idBuscado) {
    	Pedido pedidoEncontrado = pedidosRecientes.buscar(idBuscado);
    	if(pedidoEncontrado != null) {
    		System.out.println("\nPedido encontrado en la cola de recientes:");
    		System.out.println(pedidoEncontrado.getInfo());
    	}else {
    		System.out.println("Pedido con ID " + idBuscado + " no encontrado en la cola de pedidos recientes");
    	}
    }
    
    public int getTotalPedidosProcesados() {
    	return totalPedidosProce;
    }
    
    public double getValorTotalVendido() {
    	return valorTotalVendido;
    }
    
    public int getPedidosPendientes() {
    	return pedidosRecientes.sizeOf();
    }
    
    public void setGestionProductos(GestionProductos gestionProductos) {
        this.gestionProductos = gestionProductos;
    }
    
    public void setGestionEnvios(GestionEnvios gestionEnvios) {
        this.gestionEnvios = gestionEnvios;
    }

}