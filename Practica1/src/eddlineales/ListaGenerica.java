package eddlineales;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaGenerica<T> {
	private NodoGenerico<T> cabeza;
    private NodoGenerico<T> cola;
    private int tamaño;
    
    /**
     * construye una lista generica, es decir de cualquie tipo de dato que se
     * desee, el tipo de dato que guardara la lista se debe poner entre "<" y ">"
     */
    public ListaGenerica() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }
    
    /**
     * inserta al final de la lista
     * @param dato el dato a insertar al final
     */
    public void insertarAlFinal(T dato) {
    	NodoGenerico<T> nuevoNodo = new NodoGenerico<>(dato);
        
        if (estaVacia()) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.setSiguiente(nuevoNodo);
            cola = nuevoNodo;
        }
        tamaño++;
    }
    
    /**
     * inserta al principio de la lista
     * @param dato el dato a insertar al inicio
     */
    public void insertarAlInicio(T dato) {
    	NodoGenerico<T> nuevoNodo = new NodoGenerico<>(dato);
        
        if (estaVacia()) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            nuevoNodo.setSiguiente(cabeza);
            cabeza = nuevoNodo;
        }
        tamaño++;
    }
    
    /**
     * Inserta un elemento en la posicion indicada, si el dato se encuetra fuera del rango de
     * la lista mandara una excepcion del tipo IndexIndexOutOfBoundsException
     * @param posicion la posicion en la que se desea insertar el nuevo dato
     * @param dato el dato a insertar
     */
    public void insertarEnPosicion(int posicion, T dato) {
        if (posicion < 0 || posicion > tamaño) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
        
        if (posicion == 0) {
            insertarAlInicio(dato);
        } else if (posicion == tamaño) {
            insertarAlFinal(dato);
        } else {
        	NodoGenerico<T> nuevoNodo = new NodoGenerico<>(dato);
        	NodoGenerico<T> actual = cabeza;
            
            for (int i = 0; i < posicion - 1; i++) {
                actual = actual.getSiguiente();
            }
            
            nuevoNodo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevoNodo);
            tamaño++;
        }
    }
    
    /**
     * Elimina el ultimo elemento de la lista y lo devuelve despues de haberlo eliminado;
     * regresa una excepcion del tipo NoSuchElementException si la lista esta vacia
     * @return el elemento que se elimino de la lista
     */
    public T eliminarAlFinal() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        
        T datoEliminado = cola.getDato();
        
        if (tamaño == 1) {
            cabeza = null;
            cola = null;
        } else {
        	NodoGenerico<T> actual = cabeza;
            while (actual.getSiguiente() != cola) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(null);
            cola = actual;
        }
        tamaño--;
        return datoEliminado;
    }
    
    /**
     * Elimina el primer elemento de la lista y lo regresa despues de eliminarlo;
     * regresa una excepcion del tipo NoSuchElementException si la lista esta vacia
     * @return el elemento eliminado
     */
    public T eliminarAlInicio() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        
        T datoEliminado = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        
        if (cabeza == null) {
            cola = null;
        }
        tamaño--;
        return datoEliminado;
    }
    
    /**
     * Elimina un elemento de la lista en la posicion indicada y regresa el 
     * elemento despues de haberlo eliminado; 
     * regresa una excepcion del tipo IndexOutOfBoundsException si se desea eliminar
     * fuera del rango
     * @param posicion la posicion del elemento que se desea elmimiar
     * @return
     */
    public T eliminarEnPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamaño) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
        
        if (posicion == 0) {
            return eliminarAlInicio();
        } else if (posicion == tamaño - 1) {
            return eliminarAlFinal();
        } else {
        	NodoGenerico<T> actual = cabeza;
            for (int i = 0; i < posicion - 1; i++) {
                actual = actual.getSiguiente();
            }
            
            NodoGenerico<T> nodoEliminar = actual.getSiguiente();
            T datoEliminado = nodoEliminar.getDato();
            actual.setSiguiente(nodoEliminar.getSiguiente());
            tamaño--;
            return datoEliminado;
        }
    }
    
    /**
     * Regresa el elemento de la lista que se encuentra en la posicion indicada
     * @param posicion la posicion del elemento que se desea recuperar
     * @return el objeto de la lista que se encuentra en la posicion indicada
     */
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamaño) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
        
        NodoGenerico<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }
    
    /**
     * Responde si la lista no tiene elementos
     * @return true si la lista no tiene elmentos, false en cualquier otro caso
     */
    public boolean estaVacia() {
        return tamaño == 0;
    }
    
    /**
     * regresa el numero de elemento en la lista
     * @return el numero de elementos en la lista
     */
    public int tamano() {
        return tamaño;
    }
    
    /**
     * Elimina todos los elementos de la lista
     */
    public void vaciar() {
        cabeza = null;
        cola = null;
        tamaño = 0;
    }
    
    /**
     * Responde si el el elmento dato se encuetnra dentro de la lista
     * @param dato el elemento que se desea saber si se encuentra dentro de la lista
     * @return true si esta dentro de la lista, false en cualquier otro caso
     */
    public boolean contiene(T dato) {
    	NodoGenerico<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
    
    /**
     * Convierte a una representacion de cadena la lista
     * @return una cadena que es la representacion de todos los elementos de la lista
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        NodoGenerico<T> actual = cabeza;
        
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(", ");
            }
            actual = actual.getSiguiente();
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Metodo para regresar un iterador de la lista
     * @return un Iterator de la lista
     */
    public Iterator<T> iterator() {
        return new IteradorLista();
    }
    
    /**
     * Clase privada que implementa la interfaz iteratos para regresar un iterador
     */
    private class IteradorLista implements Iterator<T> {
        private NodoGenerico<T> actual = cabeza;
        
        @Override
        public boolean hasNext() {
            return actual != null;
        }
        
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T dato = actual.getDato();
            actual = actual.getSiguiente();
            return dato;
        }
    }
    
    //a partir de aqui puedes agregar tu codigo
    
    /**
     * Metodo para editar un dato en cierta posicion
     * @return el dato que se edito
     */
    public void editar(int posicion, T nuevoDato) {
    	if(posicion < 0 || posicion >= tamano()) {
    		throw new IndexOutOfBoundsException("Posicion invalida para editar: " + posicion);
    	}
    	NodoGenerico<T> actual = cabeza;
    	for(int i = 0; i < posicion; i++) {
    		actual = actual.getSiguiente();
    	}
    	actual.setDato(nuevoDato);
    }
    
    /**
     * Obtiene el indice de un elemento en la lista
     * @param datoB el dato cuyo i se quiere obtener
     * @return el i del dato
     */
    public int obtenerIndice(T datoB) {
    	NodoGenerico<T> actual = cabeza;
    	int i = 0;
    	while(actual != null) {
    		if(actual.getDato() == datoB) {
    			return i;
    		}
    		actual = actual.getSiguiente();
    		i++;
    	}
    	return -1;
    }
}
