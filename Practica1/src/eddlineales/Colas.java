package eddlineales;

import spp.Pedido;

public class Colas<T> {
	
	private NodoGenerico<T> frente;
	private NodoGenerico<T> fin;
	private int tamano;
	
	/**
	 * constructor sin parametros para una cola
	 */
	public Colas() {
		frente = null;
		fin = null;
		tamano = 0;
	}
	/**
	 * Agregamos un nuevo elemento al final de la cola
	 * @param dato el dato a agregar a la cola
	 */
	public void enqueue(T dato) {
		NodoGenerico<T> nuevo = new NodoGenerico<>(dato);
		if(isEmpty()) {
			frente = nuevo;
			fin = nuevo;
		}else {
			fin.setSiguiente(nuevo);
			fin = nuevo;
		}
		tamano++;
	}
	/**
	 * Obtenemos el primer elemento de la cola
	 * @return la cadena que corresponde al primer elemento de la cola
	 */
	public T dequeue() {
		if(isEmpty()) {
			return null;
		}
		T dato = frente.getDato();
		frente = frente.getSiguiente();
		if(frente == null) {
			fin = null;
		}
		tamano--;
		return dato;
	}
	/**
	 * Metodo que devuelve el valor del primer elemento de la cola 
	 * sin eliminarlo
	 * @return el primer dato
	 */
	public T peek() {
		if(isEmpty()) {
			return null;
		}
		return frente.getDato();
	}
	
	/**
	 * Devuelve el tamano que ocupa el dato
	 * @return el dato
	 */
	public int getTamano() {
		return tamano;
	}
	
	/**
	 * Metodo que devuelve el dato 
	 * indica si la cola no contiene ningún elemento
	 * @return
	 */
	public boolean isEmpty() {
		return frente == null;
	}
	
	public int sizeOf() { 
		return getTamano();
	}
	
	public Pedido buscarPorId(String id) {
		NodoGenerico<T> actual = frente;
		while(actual != null) {
			if(actual.getDato() instanceof Pedido) {
				Pedido p = (Pedido) actual.getDato();
				if(p.getIdPedido().equals(id)) { 
					return p;
				}
			}
			actual = actual.getSiguiente();
		}
		return null;
	}
	public Pedido buscar(String id) {
		return buscarPorId(id);
	}
}

