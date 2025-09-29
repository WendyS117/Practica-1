package uso;

import eddlineales.Lista;
import spp.Producto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase para gestionar el inventario usando Bucket Sort con listas enlazadas.
 * Cada categoría es un bucket (Lista<Producto>), ordenado por SKU dentro del bucket.
 */
public class GestionProductos {

    private Lista<String> categorias;  // Lista de categorías únicas
    private Lista<Lista<Producto>> buckets;  // Lista de buckets (cada uno una Lista<Producto> ordenada por SKU)

    public GestionProductos() {
        categorias = new Lista<>(); 
        buckets = new Lista<>();
    }

    /**
     * Carga productos desde "productos.txt" y los organiza en buckets por categoría,
     * ordenando cada bucket por SKU.
     */
    public void cargarYOrganizar() {
        cargarProductos(); 
        System.out.println("Optimizado: " + categorias.getTam() + " cat (O(1)), " + getTotalProductos() + " prod O(N) memoria/tiempo.");
    }

    private void cargarProductos() {
        try (BufferedReader br = new BufferedReader(new FileReader("productos.txt"))) {
            String linea;
            int total = 0;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] partes = linea.split(" ; ", -1);
                if (partes.length >= 5) {  // Al menos sku, nombre, categoria, precio, stock
                    try {
                        String sku = partes[0].trim();
                        String nombre = partes[1].trim();
                        String categoria = partes[2].trim();
                        double precio = Double.parseDouble(partes[3].trim());
                        int stock = Integer.parseInt(partes[4].trim());
                        int vendidos = (partes.length > 5) ? Integer.parseInt(partes[5].trim()) : 0;  // Opcional si hay vendidos

                        Producto p = new Producto(sku, nombre, categoria, precio, stock);
                        agregarProductoOrdenado(p);
                        total++;
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        // Ignorar líneas mal formateadas sin detener la carga
                    }
                }
            }
            System.out.println("Carga O(N): " + total + " productos en buckets enlazados.");
        } catch (IOException e) {
            System.err.println("Error lectura: " + e.getMessage());
        }
    }

    /**
     * Agrega un producto al bucket correspondiente por categoría.
     * Si la categoría no existe, crea un nuevo bucket.
     * Mantiene el orden por SKU dentro del bucket (inserción ordenada).
     * @param p El producto a agregar.
     */
    private void agregarProductoOrdenado(Producto p) {
        String categoria = p.getCategoria();
        int indiceCategoria = -1;
        for (int i = 0; i < categorias.getTam(); i++) {
            if (categorias.obtener(i).equals(categoria)) {
                indiceCategoria = i;
                break;
            }
        }
        Lista<Producto> bucket;
        if (indiceCategoria == -1) {
            // Nueva categoría: agregar a listas y crear bucket vacío
            categorias.agregar(categoria);
            indiceCategoria = categorias.getTam() - 1;
            bucket = new Lista<>();
            // Asegurar que buckets tenga espacio (agregar si necesario)
            while (buckets.getTam() <= indiceCategoria) {
                buckets.agregar(new Lista<>());  // Llenar gaps con buckets vacíos si es necesario (raro, pero robusto)
            }
            buckets.editar(indiceCategoria, bucket);  // Reemplazar/insertar el bucket
        } else {
            // Categoría existente: obtener bucket
            bucket = (Lista<Producto>) buckets.obtener(indiceCategoria);
            if (bucket == null) {
                bucket = new Lista<>();
                buckets.editar(indiceCategoria, bucket);
            }
        }

        // Inserción ordenada por SKU en el bucket (Bucket Sort paso 2: ordenar dentro del bucket)
        int pos = 0;
        while (pos < bucket.getTam()) {
            Producto actual = (Producto) bucket.obtener(pos);
            if (actual.getSku().compareTo(p.getSku()) > 0) {  // Si actual > p, insertar aquí
                break;
            }
            pos++;
        }
        bucket.insertar(pos, p);
    }

    /**
     * Obtiene el total de productos en todos los buckets.
     * @return Número total de productos.
     */
    public int getTotalProductos() {
        int total = 0;
        for (int i = 0; i < buckets.getTam(); i++) {
            Lista<Producto> bucket = (Lista<Producto>) buckets.obtener(i);
            if (bucket != null) total += bucket.getTam();
        }
        return total;
    }

    /**
     * Imprime categorías (para verificación).
     */
    public String imprimirCategorias() {
        return categorias.imprimeLista();
    }

    /**
     * Imprime productos de un bucket (para verificación del orden).
     * @param categoria La categoría a imprimir.
     * @return Lista de productos ordenados por SKU.
     */
    public String imprimirBucket(String categoria) {
        int indice = -1;
        for (int i = 0; i < categorias.getTam(); i++) {
            if (categorias.obtener(i).equals(categoria)) {
                indice = i;
                break;
            }
        }
        if (indice == -1) return "Categoría no encontrada.";
        Lista<Producto> bucket = (Lista<Producto>) buckets.obtener(indice);
        if (bucket == null || bucket.esVacia()) return "Bucket vacío.";
        return bucket.imprimeLista();  // Debe mostrar SKUs en orden alfabético
    }
}

