package eddlineales;

public class Pilas<T> {
	
	private NodoGenerico<T> inicio;
	private int tam;
	
	/**
	 * Constructor vacio para una pila
	 */
	public Pilas() {
		inicio = null;
		tam = 0;
	}
	
	/**
	 * Constructor con parametros para una pila
	 * @param dato
	 */
	public Pilas(T dato) {
		inicio = new NodoGenerico<>(dato);
		tam = 1;
	}
	/**
	 * Metodo que agrega un nuevo elemento a la pila
	 * (cima) de la estructura
	 * @param dato el dato que se agrega
	 */
	public void push(T dato) {
		NodoGenerico<T> nuevo = new NodoGenerico<>(dato);
		if(inicio != null) {
			nuevo.setSiguiente(inicio);
		}
		inicio = nuevo;
		tam++;
	}
	
	/**
	 * Metodo que elimina y devuelve el elemento de la 
	 * cima
	 * @return el dato eliminado
	 */
	public T pop() {
		if(inicio == null) {
			return null;
		}
		NodoGenerico<T> aux = inicio;
		inicio.getSiguiente();
		aux.setSiguiente(null);
		tam--;
		return aux.getDato();
	}
	
	/**
	 * Metodo que devuelve el valor del elemento superior de la pila 
	 * sin eliminarlo
	 * @return el dato superior de la lista sin eliminarlo
	 */
	public T peek() {
		if(inicio == null) {
			return null;
		}
		return inicio.getDato();
	}
	
	/**
	 * Metodo que devuelve el dato 
	 * indica si la cola no contiene ningún elemento
	 * @return
	 */
	public boolean isEmpty() {
		return tam == 0;
	}
	
	/**
	 * Devuelve el tamano que ocupa el dato
	 * @return el dato
	 */
	public int size() {
		return tam;
	}

}
