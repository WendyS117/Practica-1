package uso;

import eddlineales.Lista; // Tu implementación de Lista
import spp.Producto;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner; // Permitido para la interacción del menú

/**
 * Clase para gestionar el inventario de productos.
 * Utiliza una "lista de listas" (similar a Bucket Sort) donde cada categoría
 * tiene su propia lista de productos, y cada lista de productos está ordenada por SKU.
 */
public class GestionProductos {

    private Lista<String> categorias; // Lista de las categorías existentes
    private Lista<Lista<Producto>> buckets; // Lista de listas, donde cada sub-lista es un "bucket" de productos por categoría

    /**
     * Constructor por defecto. Inicializa las listas de categorías y buckets.
     */
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
        System.out.println("Inventario cargado y organizado. Total de categorías: " + categorias.getTam() + ", Total de productos: " + getTotalProductos() + ".");
    }

    /**
     * Lee el archivo "productos.txt" línea por línea para crear objetos Producto
     * y agregarlos a la estructura de inventario.
     */
    private void cargarProductos() {
        int totalProductosCargados = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("productos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim(); // Elimina espacios en blanco al inicio y final
                if (linea.isEmpty()) continue; // Ignora líneas vacías
                
                // El formato esperado es: SKU ; Nombre ; Categoria ; Precio ; Stock [; Vendidos]
                String[] partes = linea.split(" ; ", -1); // Divide la línea por " ; "
                
                // Asegurarse de que haya al menos 5 partes (SKU, Nombre, Categoria, Precio, Stock)
                if (partes.length >= 5) {
                    try {
                        String sku = partes[0].trim();
                        String nombre = partes[1].trim();
                        String categoria = partes[2].trim();
                        double precio = Double.parseDouble(partes[3].trim());
                        int stock = Integer.parseInt(partes[4].trim());
                        // La parte de "vendidos" es opcional, si existe, la parseamos
                        int vendidos = (partes.length > 5) ? Integer.parseInt(partes[5].trim()) : 0;

                        Producto p = new Producto(sku, nombre, categoria, precio, stock);
                        
                        agregarProductoOrdenado(p); // Agrega el producto a su bucket correspondiente
                        totalProductosCargados++;
                    } catch (NumberFormatException e) {
                        System.err.println("Error de formato numérico en línea de producto: " + linea + ". Detalles: " + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error de índice al parsear línea de producto: " + linea + ". Detalles: " + e.getMessage());
                    }
                } else {
                    System.err.println("Línea de producto con formato incorrecto (menos de 5 partes): " + linea);
                }
            }
            System.out.println("Se cargaron " + totalProductosCargados + " productos en el inventario.");
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
    private void agregarProductoOrdenado(Producto p) {
        String categoria = p.getCategoria();
        // --- MODIFICACIÓN AQUÍ: Reemplazar indexOf con búsqueda manual ---
        int indiceCategoria = -1;
        for (int i = 0; i < categorias.getTam(); i++) {
            if (categorias.obtener(i).equals(categoria)) {
                indiceCategoria = i;
                break;
            }
        }

        Lista<Producto> bucket;
        if (indiceCategoria == -1) { // Si la categoría no existe
            categorias.agregar(categoria); // Agrega la nueva categoría a la lista de categorías
            indiceCategoria = categorias.getTam() - 1; // El índice es el último
            bucket = new Lista<>(); // Crea una nueva lista (bucket) para esta categoría
            
            // Asegura que la lista de buckets tenga un espacio para esta nueva categoría
            if (indiceCategoria == buckets.getTam()) {
                buckets.agregar(bucket);
            } else {
                buckets.insertar(indiceCategoria, bucket); 
            }
        } else { // Si la categoría ya existe
            bucket = buckets.obtener(indiceCategoria); // Obtiene la lista (bucket) de esa categoría
            if (bucket == null) { // En caso de que el bucket estuviera nulo por alguna razón
                bucket = new Lista<>();
                buckets.editar(indiceCategoria, bucket);
            }
        }

        // Ahora, insertamos el producto en el bucket, manteniendo el orden por SKU
        int pos = 0;
        while (pos < bucket.getTam()) {
            Producto actual = bucket.obtener(pos);
            if (esSkuMayor(actual.getSku(), p.getSku())) { 
                break; // Encontramos la posición correcta para insertar
            }
            pos++; // Avanza a la siguiente posición
        }
        bucket.insertar(pos, p); // Inserta el producto en la posición encontrada
    }

    /**
     * Compara dos SKUs lexicográficamente (alfabéticamente) sin usar String.compareTo().
     * Retorna true si sku1 es "mayor" que sku2 (es decir, sku1 debería ir después de sku2).
     * @param sku1 El primer SKU a comparar.
     * @param sku2 El segundo SKU a comparar.
     * @return true si sku1 es lexicográficamente mayor que sku2, false en caso contrario.
     */
    private boolean esSkuMayor(String sku1, String sku2) {
        int len1 = sku1.length();
        int len2 = sku2.length();
        int minLen = (len1 < len2) ? len1 : len2; 

        for (int i = 0; i < minLen; i++) {
            char char1 = sku1.charAt(i);
            char char2 = sku2.charAt(i);
            if (char1 > char2) { 
                return true;
            }
            if (char1 < char2) { 
                return false;
            }
        }
        return len1 > len2;
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
