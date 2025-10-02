package spp;

public class Producto {
    private String sku;          // Código único (ej: "ELEC-12345")
    private String nombre;
    private String categoria;    // ELECTRONICA, ROPA, ALIMENTOS, HOGAR
    private double precio;
    private int stock;
    private int vendidos;        // Contador de unidades vendidas
    
    // Constructor completo
    public Producto(String sku, String nombre, String categoria, double precio, int stock) {
        this.sku = sku;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.vendidos = 0;
    }
    
    // Getters y setters
    public String getSku() {
		return sku;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCategoria() {
		return categoria;
	}

	public double getPrecio() {
		return precio;
	}

	public int getStock() {
		return stock;
	}

	public int getVendidos() {
		return vendidos;
	}
    
    public boolean reducirStock(int cantidad) {
        if (stock >= cantidad) {
            stock -= cantidad;
            vendidos += cantidad;
            return true;
        }
        return false;
    }

	public String getInfo() {
        return String.format("%s - %s (Stock: %d, Vendidos: %d)", sku, nombre, stock, vendidos);
    }

	public String toSaveString() {
		return String.format("%s ; %s ; %s ; %.2f ; %d ; %d", sku, nombre, categoria, precio, stock, vendidos);
	}
	
	//se agrego
	

	public void setVendidos(int vendidos) {
		this.vendidos = vendidos;
	}
	

}
