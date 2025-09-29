package eddlineales;

public class NodoGenerico<T> {
	private T dato;
    private NodoGenerico<T> siguiente;
    
    public NodoGenerico(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
    
    // Getters y setters
    public T getDato() { 
    	return dato; 
    }
    public void setDato(T dato) { 
    	this.dato = dato; 
    }
    
    public NodoGenerico<T> getSiguiente() { 
    	return siguiente; 
    }
    public void setSiguiente(NodoGenerico<T> siguiente) { 
    	this.siguiente = siguiente; 
    }
}
