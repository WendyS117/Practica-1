package eddlineales;

import spp.Pedido;

public class Pilas<T> {
	
	private NodoGenerico<T> cima;
	private int tamano;
	
	/**
	 * Constructor vacio para una pila
	 */
	public Pilas() {
		cima = null;
		tamano = 0;
	}

	/**
	 * Metodo que agrega un nuevo elemento a la pila
	 * (cima) de la estructura
	 * @param dato el dato que se agrega
	 */
	public void push(T dato) {
		NodoGenerico<T> nuevo = new NodoGenerico<>(dato);
		nuevo.setSiguiente(cima);
		cima = nuevo;
		tamano++;
	}
	
	/**
	 * Metodo que elimina y devuelve el elemento de la 
	 * cima
	 * @return el dato eliminado
	 */
	public T pop() {
		if(isEmpty()) {
			return null;
		}
		T dato = cima.getDato();
		cima = cima.getSiguiente();
		tamano--;
		return dato;
	}
	
	/**
	 * Metodo que devuelve el valor del elemento superior de la pila 
	 * sin eliminarlo
	 * @return el dato superior de la lista sin eliminarlo
	 */
	public T peek() {
		if(isEmpty()) {
			return null;
		}
		return cima.getDato();
	}
	
	/**
	 * Metodo que devuelve el dato 
	 * indica si la cola no contiene ningún elemento
	 * @return
	 */
	public boolean isEmpty() {
		return cima == null;
	}
	
	/**
	 * Devuelve el tamano que ocupa el dato
	 * @return el dato
	 */
	public int size() {
		return tamano;
	}
	
	public Pedido buscarPorId(String id) {
		NodoGenerico<T> actual = cima;
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
}
