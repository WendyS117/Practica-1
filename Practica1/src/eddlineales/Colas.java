package eddlineales;


public class Colas<T> {
	
	private NodoGenerico<T> inicio;
	private NodoGenerico<T> fin;
	private int tam;
	
	/**
	 * constructor sin parametros para una cola
	 */
	public Colas() {
		inicio = null;
		fin = null;
		tam = 0;
	}
	/**
	 * Constructor con parametros para una cola
	 * @param dato
	 */
	public Colas(T dato) {
		inicio = new NodoGenerico<>(dato);
		fin = inicio;
		tam = 1;
	}
	/**
	 * Agregamos un nuevo elemento al final de la cola
	 * @param dato el dato a agregar a la cola
	 */
	public void enqueue(T dato) {
		NodoGenerico<T> nuevo = new NodoGenerico<>(dato);
		if(inicio==null) {
			inicio = nuevo;
			fin = nuevo;
		}else {
			fin.setSiguiente(nuevo);
			fin = nuevo;
		}
		tam++;
	}
	/**
	 * Obtenemos el primer elemento de la cola
	 * @return la cadena que corresponde al primer elemento de la cola
	 */
	public T dequeue() {
		if(inicio==null) {
			return null;
		}else {
			NodoGenerico<T> aux = inicio;
			inicio = inicio.getSiguiente();
			aux.setSiguiente(null);
			tam--;
			if(inicio == null) {
				fin = null;
			}
			return aux.getDato();
		}
	}
	/**
	 * Metodo que devuelve el valor del primer elemento de la cola 
	 * sin eliminarlo
	 * @return el primer dato
	 */
	public T peek() {
		if(inicio == null) {
			return null;
		}
		return inicio.getDato();
	}
	
	/**
	 * Devuelve el tamano que ocupa el dato
	 * @return el dato
	 */
	public int sizeOf() {
		return tam;
	}
	
	/**
	 * Metodo que devuelve el dato 
	 * indica si la cola no contiene ningún elemento
	 * @return
	 */
	public boolean isEmpty() {
		return inicio==null;
	}
	
	public T buscar(String idBuscado) {
		NodoGenerico<T> actual = inicio;
		while(actual != null) {
			if(actual.getDato() instanceof spp.Pedido) {
				spp.Pedido pedido = (spp.Pedido) actual.getDato();
				if(pedido.getIdPedido() == idBuscado) {
					return actual.getDato();
				}
			}
			actual = actual.getSiguiente();
		}
		return null;
	}

}
