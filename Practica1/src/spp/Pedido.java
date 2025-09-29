package spp;

import java.util.Date;
import java.util.Iterator;

import eddlineales.ListaGenerica;

public class Pedido {
	private String idPedido;
    private String cliente;
    private Date fechaProcesamiento;
    private String estado;       // PENDIENTE, PROCESADO, ENVIADO, ENTREGADO
    private double total;
    private ListaGenerica<ItemPedido> items;
    
    public Pedido(String cliente) {
        this.idPedido = "PED-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.cliente = cliente;
        this.estado = "PENDIENTE";
        this.items = new ListaGenerica<ItemPedido>();
        this.total = 0.0;
    }
    
    public void agregarItem(Producto producto, int cantidad) {
        items.insertarAlInicio(new ItemPedido(producto, cantidad));
        total += producto.getPrecio() * cantidad;
    }
    
    public String getInfo() {
        return  String.format("%s %s Total: $%.2f Estado: %s ", idPedido, cliente, total, estado);
    }
    
    public String toSaveString() {
        String res =  String.format("%s ; %s ; %s ; ", idPedido, cliente, estado);
        Iterator<ItemPedido> itPedido = items.iterator();
        while(itPedido.hasNext()) {
        	ItemPedido ip = itPedido.next(); 
        	res += ip.getProducto().getSku()+" "+ip.getCantidad()+" | ";
        }
        return res;
    }

}
