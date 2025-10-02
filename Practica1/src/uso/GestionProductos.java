package uso;

import eddlineales.*;
import spp.*;
import java.io.*;
import java.util.Iterator;


public class GestionProductos {
	
	private ListaGenerica<String> categorias;
	private ListaGenerica<ListaGenerica<Producto>> buckets;
	private GestionEnvios gestionEnvios;
	private GestionPedidos gestionPedidos;
	
	public GestionProductos() {
		categorias = new ListaGenerica<String>();
		buckets = new ListaGenerica<>();
		gestionEnvios = null;
		gestionPedidos = null;
	}
	/**
	 * Carga y organiza los productos desde el archivo "productos.txt"
	 */
	public void cargaYOrganizaProdu() {
		int totalProduC = 0;
		boolean saltarHeader = true;
		try(BufferedReader br = new BufferedReader(new FileReader("productos.txt"))){
			String linea;
			while((linea = br.readLine()) != null) {
				linea = linea.trim();
				if(linea.isEmpty()) continue;
				
				if(saltarHeader) {
					saltarHeader = false;
					if(linea.toLowerCase().startsWith("sku ;")) {
						continue;
					}
				}
				
				String[] partes = linea.split(" ; ", -1);
				if(partes.length >= 5) {
					try {
						String sku = partes[0].trim();
						String nombre = partes[1].trim();
						String categoria = partes[2].trim().toUpperCase();
						double precio = Double.parseDouble(partes[3].trim());
						int stock = Integer.parseInt(partes[4].trim());
						
						int vendidos = 0;
						if(partes.length >= 6) {
							vendidos = Integer.parseInt(partes[5].trim());
						}
						
						Producto nuevoProducto = new Producto(sku, nombre, categoria, precio, stock);
						nuevoProducto.setVendidos(vendidos);
						
						//Para organizar por categoria (se uso bucket sort)
						int indiceCate = categorias.obtenerIndice(categoria);
						if(indiceCate == -1) {
							//Se añade nueva categoria y crea un nuevo bucket
							categorias.insertarAlFinal(categoria);
							ListaGenerica<Producto> nuevoBucket = new ListaGenerica<>();
							nuevoBucket.insertarAlFinal(nuevoProducto);
							buckets.insertarAlFinal(nuevoBucket);
						}else {
							//Si existe la categoria, se añade al bucket que corresponde
							buckets.obtener(indiceCate).insertarAlFinal(nuevoProducto);
						}
						totalProduC++;
					}catch (NumberFormatException e) {
						System.err.println("Error de formato numerico en linea: " + linea);
					}
				}else {
					System.err.println("Error: formato de linea de producto invalido: " + linea);
				}
			}
			System.out.println("Se cargaron y organizaron " + totalProduC + " productos");
		}catch (IOException e) {
			System.err.println("Error al leer productos.txt: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo para actualizar el stock de un producto dado el SKU
	 * @param sku el SKU del producto
	 * @param cantidad La cantidad a reducir del stock
	 * @return true si el stock se redujo, false si no hay suficiente stock o no exis
	 * el producto
	 */
	public boolean actualizarStock(String sku, int cantidad) {
		for(int i = 0; i < buckets.tamano(); i++) {
			ListaGenerica<Producto> bucket = buckets.obtener(i);
			var it = bucket.iterator();
			while(it.hasNext()) {
				Producto p = it.next();
				if(p.getSku().equals(sku)) {
					return p.reducirStock(cantidad);
				}
			}
		}
		return false; //por si no se encontro el producto
	}
	
	/**
	 * Metodo para buscar productos por categoria
	 * @param categoriaBusc la categoria que se busca
	 */
	public void buscarProducPorCatego(String categoriaBusc) {
		String cat = categoriaBusc.toUpperCase();
		int iCate= categorias.obtenerIndice(cat);
		if(iCate != -1) {
			ListaGenerica<Producto> productosEnCate = buckets.obtener(iCate);
			if(productosEnCate.estaVacia()) {
				System.out.println("No hay productos en la categoria " + categoriaBusc + " ");
			}else {
				System.out.println("\n--- Productos en la categoria: " + cat + " ---");
				var it = productosEnCate.iterator();
				while(it.hasNext()) {
					System.out.println(it.next().getInfo());
				}
			}
		}else {
			System.out.println("Categoria " + categoriaBusc + " no encontrada");
		}
	}
	
	public void generarReporteInventario() {
		System.out.println("=== REPORTE FINAL DEL SISTEMA ===");
		if(categorias.estaVacia()) {
			System.out.println("El inventario esta vacio.");
			return;
		}
		
		int totalProdu = 0;
		double valorInventario = 0.0;
		int stockCritico = 0;
		Producto productoMasVendido = null;
		int maxVendidos = -1;
		
		for(int i = 0; i<buckets.tamano(); i++) {
			ListaGenerica<Producto> productosEnCate = buckets.obtener(i);
			Iterator<Producto> it = productosEnCate.iterator();
			while(it.hasNext()) {
				Producto p = it.next();
				totalProdu++;
				valorInventario += p.getPrecio() * p.getStock();
				if(p.getStock() < 10) {//stock critico son < 10 unidades
					stockCritico++;
				}
				if(p.getVendidos() > maxVendidos) {
					maxVendidos = p.getVendidos();
					productoMasVendido = p;
				}
			}
		}
		
		System.out.println("\nPRODUCTOS:");
		System.out.println("- Total productos: " + totalProdu);
		System.out.printf("- Valor inventario: $%.2f\n", valorInventario);
		System.out.println("- Stock critico: " + stockCritico + " productos");
		if(productoMasVendido != null) {
			System.out.println("- Producto mas vendido: " + productoMasVendido.getSku() + " - " + productoMasVendido.getNombre() + " (" + productoMasVendido.getVendidos() + " unidades)");
		}else {
			System.out.println("- Producto mas vendido: N/A (No hay ventas registradas)");
		}
		System.out.println("\nPEDIDOS:");
		int totalProcesados = gestionPedidos.getTotalPedidosProcesados();
		double valorVendido = gestionPedidos.getValorTotalVendido();
		int pedidosEnPilaReciente = gestionEnvios.getPedidosEnPilaParaEnvio();
		System.out.println("- Total pedidos procesados : " + (totalProcesados));
		System.out.println("- Pedidos en pila reciente : " + (pedidosEnPilaReciente));
		System.out.println("- Valor total vendido : " + (valorVendido));

		System.out.println();
		System.out.println("ENVÍOS :");
		
		int pedidosEnColaEnvio = pedidosEnPilaReciente; 
		System.out.println("- Pedidos en cola de envío : " + (pedidosEnColaEnvio));
	}
	
	public void setGestionEnvios(GestionEnvios gestionEnvios) {
	    this.gestionEnvios = gestionEnvios;
	}
	public void setGestionPedidos(GestionPedidos gestionPedidos) {
	    this.gestionPedidos = gestionPedidos;
	}

}
