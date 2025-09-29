package eddlineales;

public class Lista<T> {
	
	private NodoGenerico<T> inicio;
	private int tam;
	
	//Constructor vacio
	public Lista() {
		inicio = null;
		tam = 0;
	}
	
	//Constructor con parametros
	public Lista(T dato) {
		inicio = new NodoGenerico<>(dato);
		tam = 1;
	}
	/**
	 * Metodo para agregar el dato al final de la lista
	 * @param dato el dato que se va a agregar
	 */
	public void agregar(T dato) {
		NodoGenerico<T> nuevo = new NodoGenerico<>(dato);
		if(esVacia()) {
			inicio = nuevo;
		}else {
			NodoGenerico<T> aux = inicio;
			while(aux.getSiguiente() != null) {
				aux = aux.getSiguiente();
			}
			aux.setSiguiente(nuevo);
		}
		tam++;
	}
	
	/**
	 * metodo  para eliminar el dato si esta al inicio
	 * o, si esta dentro de la lista busca el dato 
	 * @param dato el dato a aliminar
	 */
	public void eliminar(T dato) {
		if(esVacia()) {
			return;
		}
		//elemento al inicio
		if(inicio.getDato() == dato) {
			inicio = inicio.getSiguiente();
			return;
		}
		//si esta dentro de la lista
		NodoGenerico<T> aux = inicio;
		//se recorre la lista hasta encontar el dato
		while(aux.getSiguiente().getDato() != dato) {
			aux = aux.getSiguiente();
		}
		NodoGenerico<T> aux_2 = aux.getSiguiente().getSiguiente();
		aux.getSiguiente().setSiguiente(null);
		aux.setSiguiente(aux_2);
		tam--;
	}
	
	/**
	 * metodo para obtener el dato buscado
	 * @param pos la posicion del dato
	 * @return regresa donde esta el dato
	 */
	public T obtener(int pos) {
        NodoGenerico<T> aux = inicio;
        int cont = 0;
        while(aux != null) {
        	if(cont == pos) {
        		return aux.getDato();
        	}
        	aux = aux.getSiguiente();
            cont++;
        }
        return null;
    }
	
	/**
	 * Metodo que Imprime la lista
	 * @return la lista impresa
	 */
    public String imprimeLista() {
		String res = " ";
		NodoGenerico<T> aux = inicio;
		while(aux != null) {
			T dato = aux.getDato();
			if(dato instanceof spp.Producto) {
				res += ((spp.Producto)dato).getInfo() + ", ";
			}else {
				res += dato.toString() + ", ";
			}
			aux = aux.getSiguiente();
		}
		if(!res.isEmpty() && res.length() > 2) {
			res = res.substring(0, res.length() -2);
		}
		return res;
	}
	
	/**
	 * Metodo para Editar el valor en cierta posición
	 * @param pos la posicion del dato
	 * @param nDato el dato n que se va a editar
	 */
    public void editar(int pos, T nDato) {
    	//si la posicion es menor a 0, o si posicion es mayor igual al tamaño
        if (pos < 0 || pos >= tam) {
        	return;
        }
        NodoGenerico<T> aux = inicio;
        int i = 0;
        //Recorre hasta la posicion con un bucle
        while (i < pos) {
            aux = aux.getSiguiente();
            i++;
        }
        //el dato del auxiliar apunta a nDato
        aux.setDato(nDato);
    }
	/**
	 * Metodo para Insertar un dato en cierta posición
	 * @param pos la posicion del dato
	 * @param dato el dato a insertar
	 */
    public void insertar(int pos, T dato) {
    	//si la posicion es mayor al tamaño, agrega al dato al final
        if (pos>tam) {
            agregar(dato);
            return;
        }
        NodoGenerico<T> nuevo = new NodoGenerico<>(dato);
        NodoGenerico<T> aux = inicio;
        //si pos es igual a 0, lo inserta al inicio
        if(pos == 0) {
        	nuevo.setSiguiente(inicio);
        	inicio = nuevo;
        	tam++;
        }
        //si esta en medio, lo recorre con un bucle while
        int i = 0;
        while(aux.getSiguiente() != null && i < pos-1) {
        	aux = aux.getSiguiente();
        	i++;
        }
        nuevo.setSiguiente(aux.getSiguiente());
        aux.setSiguiente(nuevo);
        tam++;
        
    }
    /**
     * Metodo que devuelve un vacio
     * @return regresa un 0
     */
	public boolean esVacia() {
        return tam == 0;
    }
	/**
	 * Metodo para el Tamanio de la lista
	 * @return el tamanio
	 */
    public int getTam() {
        return tam;
    }
}
