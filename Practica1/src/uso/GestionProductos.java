package uso;

import eddlineales.Lista;
import spp.Producto;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class GestionProductos {

    private Lista<String> categorias;
    private Lista<Lista<Producto>> buckets;

    public GestionProductos() {
        categorias = new Lista<>(); 
        buckets = new Lista<>();
    }

    /**
     * Carga los productos desde el archivo "productos.txt" y los organiza
     * en los buckets correspondientes, manteniendo el orden por SKU dentro de cada bucket.
     */
    public void cargarYOrganizar() {
        cargarProductos(); 
        System.out.println("Inventario cargado y organizado. 
                           Total de categorías: " + categorias.getTam() + ", Total de productos: " + getTotalProductos() + ".");
    }

    /**
     * Lee el archivo "productos.txt" línea por línea para crear objetos Producto
     * y agregarlos a la estructura de inventario.
     */
    private void cargarProductos() {
        int totalProdC = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("productos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" ; ", -1);
                // Asegurarse de que haya al menos 5 partes (SKU, Nombre, Categoria, Precio, Stock)
                if (partes.length >= 5) {
                    try {
                        String sku = partes[0].trim();
                        String nombre = partes[1].trim();
                        String categoria = partes[2].trim();
                        double precio = Double.parseDouble(partes[3].trim());
                        int stock = Integer.parseInt(partes[4].trim());
                        Producto p = new Producto(sku, nombre, categoria, precio, stock);
                        agregarProductoOrdenado(p);
                        totalProdC++;
                    } catch (NumberFormatException e) {
                        System.err.println("Error de formato numérico en línea de producto: " + linea + ". Detalles: " + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error de índice al parsear línea de producto: " + linea + ". Detalles: " + e.getMessage());
                    }
                } else {
                    System.err.println("Línea de producto con formato incorrecto (menos de 5 partes): " + linea);
                }
            }
            System.out.println("Se cargaron " + totalProdC + " productos en el inventario.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo productos.txt: " + e.getMessage());
        }
    }

    /**
     * Agrega un producto a la lista (bucket) de su categoría correspondiente.
     * Si la categoría no existe, crea una nueva lista para ella.
     * Mantiene los productos ordenados por SKU dentro de cada lista de categoría.
     * @param p El producto a agregar.
     */
    public void insertar(int sku, String nombre){
        
    }

    /**
     * Calcula el número total de productos en todo el inventario.
     * @return El número total de productos.
     */
    public int getTotalProductos() {
        int total = 0;
        for (int i = 0; i < buckets.getTam(); i++) {
            Lista<Producto> bucket = buckets.obtener(i);
            if (bucket != null) total += bucket.getTam();
        }
        return total;
    }

    /**
     * Imprime todas las categorías de productos existentes.
     * @return Un String con las categorías separadas por comas.
     */
    public String imprimirCategorias() {
        return categorias.imprimeLista();
    }

    /**
     * Busca y muestra los productos de una categoría específica.
     * @param scanner Objeto Scanner para leer la entrada del usuario.
     */
    public void menuBuscarProductos(Scanner scanner) {
        System.out.println("\n--- Búsqueda de Productos por Categoría ---");
        System.out.println("Categorías disponibles: " + imprimirCategorias());
        System.out.print("Ingrese el nombre de la categoría a buscar: ");
        String categoriaBuscada = scanner.nextLine().trim().toUpperCase();

        // --- MODIFICACIÓN AQUÍ: Reemplazar indexOf con búsqueda manual ---
        int indice = -1;
        for (int i = 0; i < categorias.getTam(); i++) {
            if (categorias.obtener(i).equals(categoriaBuscada)) {
                indice = i;
                break;
            }
        }

        if (indice != -1) { // Si la categoría fue encontrada
            Lista<Producto> bucket = buckets.obtener(indice); // Obtiene la lista de productos de esa categoría
            if (bucket != null && !bucket.esVacia()) {
                System.out.println("\nProductos en la categoría '" + categoriaBuscada + "':");
                System.out.println(bucket.imprimeLista()); // Imprime los productos
            } else {
                System.out.println("No hay productos en la categoría '" + categoriaBuscada + "'.");
            }
        } else {
            System.out.println("Categoría '" + categoriaBuscada + "' no encontrada en el inventario.");
        }
    }

    /**
     * Actualiza el stock de un producto específico.
     * Se usa después de procesar un pedido para reducir la cantidad de productos disponibles.
     * @param sku El SKU (código único) del producto a actualizar.
     * @param cantidad La cantidad a reducir del stock.
     * @return true si el stock se redujo con éxito, false si el producto no se encontró o no hay suficiente stock.
     */
    public boolean actualizarStock(String sku, int cantidad) {
        // Recorre todos los buckets (categorías)
        for (int i = 0; i < buckets.getTam(); i++) {
            Lista<Producto> bucket = buckets.obtener(i);
            if (bucket != null) {
                // Recorre los productos dentro de cada bucket
                for (int j = 0; j < bucket.getTam(); j++) {
                    Producto p = bucket.obtener(j);
                    if (p.getSku().equals(sku)) { // Si encuentra el producto por SKU
                        return p.reducirStock(cantidad); // Intenta reducir el stock
                    }
                }
            }
        }
        return false; // El producto con el SKU especificado no fue encontrado
    }

    /**
     * Genera un reporte completo del estado actual del inventario.
     * Incluye el total de productos, valor monetario, productos en stock crítico y el más vendido.
     */
    public void generarReporteInventario() {
        int totalProductos = 0;
        double valorInventario = 0.0;
        int stockCriticoCount = 0;
        Producto productoMasVendido = null;
        int maxVendidos = -1; 

        for (int i = 0; i < buckets.getTam(); i++) {
            Lista<Producto> bucket = buckets.obtener(i);
            if (bucket != null) {
                for (int j = 0; j < bucket.getTam(); j++) {
                    Producto p = bucket.obtener(j);
                    totalProductos++;
                    valorInventario += p.getPrecio() * p.getStock(); 
                    if (p.getStock() < 10) { 
                        stockCriticoCount++;
                    }
                    if (p.getVendidos() > maxVendidos) { 
                        maxVendidos = p.getVendidos();
                        productoMasVendido = p;
                    }
                }
            }
        }

        System.out.println("\n=== REPORTE DE INVENTARIO ===");
        System.out.println("PRODUCTOS:");
        System.out.println("- Total productos: " + totalProductos);
        System.out.printf("- Valor total del inventario: $%.2f\n", valorInventario);
        System.out.println("- Productos en stock crítico (< 10 unidades): " + stockCriticoCount);
        if (productoMasVendido != null) {
            System.out.println("- Producto más vendido: " + productoMasVendido.getSku() + " - " + productoMasVendido.getNombre() + " (" + productoMasVendido.getVendidos() + " unidades vendidas)");
        } else {
            System.out.println("- Producto más vendido: N/A (No hay ventas registradas aún)");
        }
    }
}

