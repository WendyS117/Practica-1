package uso;

import java.util.*;

public class usoSistemaProcesamientoPedidos {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		GestionProductos gestionProductos = new GestionProductos();
		GestionEnvios gestionEnvios = new GestionEnvios();
		GestionPedidos gestionPedidos = new GestionPedidos(gestionProductos, gestionEnvios);
		
		gestionProductos.setGestionEnvios(gestionEnvios);
		gestionProductos.setGestionPedidos(gestionPedidos);
		
		int menuPrincipal;
		
		do {
			System.out.println("\n=== MENU PRINCIPAL ===");
			System.out.println("1. Gestion de productos");
			System.out.println("2. Procesamiento de pedidos");
			System.out.println("3. Sistema de envios");
			System.out.println("4. Salir");
			System.out.print("Seleccione una opcion: ");
			
			menuPrincipal = leer(scanner);
			
			switch(menuPrincipal){
			case 1:
				int productos;
				do {
					System.out.println("\n=== GESTION DE PRODUCTOS ===");
					System.out.println("1. Cargar y organizar el inventario");
					System.out.println("2. Buscar productos por categoria");
					System.out.println("3. Generar reporte de inventario");
					System.out.println("4. Volver al menu principal");
					System.out.print("Seleccione una opcion: ");
					
					productos = leer(scanner);
					
					switch(productos) {
					case 1:
						gestionProductos.cargaYOrganizaProdu();
						break;
					case 2:
						System.out.print("Ingrese la categoria que busca: ");
						String categoria = scanner.nextLine().trim();
						gestionProductos.buscarProducPorCatego(categoria);
						break;
					case 3:
						gestionProductos.generarReporteInventario();
						break;
					case 4:
						break;
					default:
						System.out.println("Opcion invalida.");
					}
				}while(productos != 4);
				break;
				
			case 2:
				int pedidos;
				do {
					System.out.println("\n=== PROCESAMIENTO DE PEDIDOS ===");
					System.out.println("1. Cargar pedidos iniciales");
					System.out.println("2. Procesar los ultimos pedidos");
					System.out.println("3. Buscar pedido por ID");
					System.out.println("4. Ver cantidad de pedidos pendientes");
					System.out.println("5. Volver al menu principal");
					System.out.print("Seleccione una opcion: ");
					pedidos = leer(scanner);
					
					switch (pedidos) {
					case 1:
						gestionPedidos.cargarPedidosIniciales();
						break;
					case 2:
						gestionPedidos.procesarPedidos(6500);
						break;
					case 3:
						System.out.print("Ingrese el ID del pedido que se quiera buscar: ");
						String idPedido = scanner.nextLine().trim();
						gestionPedidos.buscarPedido(idPedido);
						break;
					case 4:
						System.out.println("Pedidos pendientes: " + gestionPedidos.getPedidosPendientes());
						break;
					case 5:
						break;
					default:
						System.out.println("Opcion invalida.");
					}
				}while(pedidos != 5);
				break;
				
			case 3:
				int envios;
				do {
					System.out.println("\n=== SISTEMA DE ENVIOS ===");
					System.out.println("1. Procesar envios pendientes");
					System.out.println("2. Generar reporte de envios pendientes");
					System.out.println("3. Volver al menu principal");
					System.out.print("Seleccione una opcion: ");
					envios = leer(scanner);
					
					switch(envios) {
					case 1:
						gestionEnvios.procesarEnvios();
						break;
					case 2:
						gestionEnvios.generarReporteEnviosPendientes();
						break;
					case 3:
						break;
					default:
						System.out.println("Opcion invalida");
					}
				}while(envios != 3);
				break;
				
			case 4:
				System.out.println("Saliendo del sistema.");
				break;
			default:
				System.out.println("Opcion invalida");
			}
		}while(menuPrincipal != 4);
		scanner.close();
		System.out.println("Se ha salido del sistema.");

	}
	
	private static int leer(Scanner scanner) {
		while(true) {
			try {
				return Integer.parseInt(scanner.nextLine());
			}catch(NumberFormatException e) {
				System.err.println("Entrada invalida. Ingrese un numero entero: ");
			}
		}
	}

}
