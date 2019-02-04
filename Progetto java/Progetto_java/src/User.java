/*
 * Federico Germinario 545081
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class User<E> {
	// Overview: tipo modificabile che rappresenta un utente con i relativi dati
    // AF = <username, password, {dati.get(0), ...,dati.get(i)}> con dati.get(i) = {owner-i, E data-i} per 0 <= i < dati.size()
    // IR = Username != null && password != null && dati != null 
	//      && for all i. 0 <= i < dati.size() ==> ( (dati.get(i) != null) && (dati.get(i).owner != null) && (dati.get(i).data != null) )
	
	private String username;
	private String password;
	private List<DataClass<E>> dati;
	
	// Inizializzo le variabili d'istanza 
	public User(String username, String password) {
		if(username == null | password == null) throw new NullPointerException();
		this.username = username;
		this.password = password;
		dati = new ArrayList<DataClass<E>>();
	}
	
	// Metodo che permette l'autenticazione
	public User<E> login(String usr, String passw) {
		if(usr.equals(username) && passw.equals(password)) {
			return this;
		}
		return null;
	}
	
	public boolean addDato(DataClass<E> d) { 
		return dati.add(d);
	}
	
	public int getNElementi() {
		return dati.size();
	}
	
	// Restituisce il dato se è presente in dati, altrimenti null
	public E getDato(E d) {                       
		for (DataClass<E> tmpD : dati) {
			E tmpDato =  tmpD.getDato();
			if(tmpDato.equals(d)) {
				return tmpDato;
			}
		}
		return null;
	}
	
	// Restituisce un oggetto di tipo DataClass<E> contenente il dato d , altrimenti null
	public DataClass<E> getDataClass(E d) {                      
		for (DataClass<E> tmpD : dati) {
			if(tmpD.getDato().equals(d)) {
				return tmpD;
			}
		}
		return null;
	}
	
	// Rimuove tute le occorenze di d da dati
	public boolean removeDato(E d) {   
		return dati.removeAll(Collections.singleton(getDataClass(d)));
	}
	
	// Modifica la password di this se le credenziali sono corrette
	public boolean setPassw(String Id, String oldPassw ,String newPassw) {
		if(this.username.equals(Id) && this.password.equals(oldPassw)) {
			this.password = newPassw;
			return true;
		}
		return false;
	}
	
	// Restituisce un iteratore su dati (senza remove) 
	public Iterator<E> getIterator(){
		return new Iterator<E>() {
			private int i = -1;

			@Override
			public boolean hasNext() {
				return (i < dati.size() - 1);
			}

			@Override
			public E next() {
				if(hasNext()) {
					i++;
					return dati.get(i).getDato();
				}else {
					return null;
				}
			}
	
			public void remove() throws UnsupportedOperationException{
				throw new UnsupportedOperationException();
			}
		
		};
	}
	
	@Override
    public boolean equals(Object obj) {
		if(obj instanceof User<?>) {
			if( ((User<?>) obj).username.equals(username) ) {
				return true;
			}
		}
		return false;
	}
	
}
