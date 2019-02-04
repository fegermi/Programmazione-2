/*
 * Federico Germinario 545081
 */

import java.util.Iterator;

public interface SecureDataContainer<E>{
	/* Overview: tipo modificabile che rappresenta una collezione di utenti con password e i loro dati di tipo E
	 * Typical Element: {u1, ..., un}, {<user-0, password-0, {data-0,...,data-i}>,...,<user-n, password-n, {data-0,...,data-k}>}
	 * 					dove user-i != user-j per ogni i,j tale che i != j
	 */
	
	/* @REQUIRES: Id != null && passw != null
	 * @MODIFIES: this 
	 * @THROWS: se Id = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 *          se u = <Id, _ , _ > appartiene a {u1, ...,un} lancia una UserAlreadyExistException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: {u1, ..., un} U unew = <Id, passw, { }>
	 */
	// Crea l’identità un nuovo utente della collezione
	public void createUser(String Id, String passw) throws UserAlreadyExistException;
	
	
	/* @REQUIRES: Id != null && passw != null
	 * @MODIFIES: this
	 * @THROWS: se Id = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Id, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Id, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: {u1, ..., un} / u = <Id, passw, dati>   
	 */
	// Elimina un utente dalla collezione
	public void removeUser(String Id, String passw) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Id != null && oldPassw != null && newPassw != null
	 * @MODIFIES: this
	 * @THROWS: se Id = null || oldPassw = null || newPassw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Id, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Id, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: u.password = newPassw tale che u = <Id, password, dati> appartente a {u1, ..., un} 
	 */
	// Cambia la password di un utente della collezione
	public void changePassword(String Id, String oldPassw, String newPassw) throws NotFoundUserException, LoginFailException; 
	
	
	/* @REQUIRES: Owner != null && passw != null
	 * @MODIFIES: - 
	 * @THROWS: se Owner = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Owner, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: restituisce {#(u.dati) | u = <Owner, passw, dati> appartente a {u1, ...,un}}
	 */
	// Restituisce il numero degli elementi di un utente presenti nella collezione
	public int getSize(String Owner, String passw) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: this
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Id, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: inserisce data in u.dati tale che u = <Owner, passw, dati> appartente a {u1, ..., un}
	 */ 
	// Inserisce il valore del dato nella collezione se vengono rispettati i controlli di identità
	public boolean put(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: - 
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Owner, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: restituisce la prima occorenza di data presente nell'insieme dei dati di Owner, altrimenti null
	 */
	// Ottiene una copia del valore del dato nella collezione se vengono rispettati i controlli di identità
	public E get(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: this
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Owner, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: rimuove tutte le occorenze di data presenti nell'insieme dei dati di Owner, altrimenti null
	 */
	// Rimuove il dato nella collezione se vengono rispettati i controlli di identità
	public E remove(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: this
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Owner, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: crea una copia di data nell'insieme dei dati di Owner se data è presente in quel insieme, altrimenti non fa nulla  
	 */
	// Crea una copia del dato nella collezione se vengono rispettati i controlli di identità
	public void copy(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Owner != null && passw != null && Other != null && data != null
	 * @MODIFIES: this
	 * @THROWS: se Owner = null || passw = null || Other = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Owner, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: aggiunge data nell'insieme dei dati di Other 
	 */
	// Condivide il dato nella collezione con un altro utente se vengono rispettati i controlli di identità
	public void share(String Owner, String passw, String Other, E data) throws NotFoundUserException, LoginFailException;
	
	
	/* @REQUIRES: Owner != null && passw != null 
	 * @MODIFIES: -
	 * @THROWS: se Owner = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se u = <Owner, _ , _ > non appartiene a {u1, ...,un} lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se {passw != u.password | u = <Owner, password, dati> appartente a {u1, ..., un}} lancia una LoginFailException (eccezione non disponibile in Java, checked) 
	 * @EFFECTS: restituisce un iteratore (senza remove) che genera tutti i dati di Owner in ordine arbitrario
	 */
	// Restituisce un iteratore (senza remove) che genera tutti i dati dell’utente in ordine arbitrario
	// se vengono rispettati i controlli di identità
	public Iterator<E> getIterator(String Owner, String passw) throws NotFoundUserException, LoginFailException;
	
	
	// @EFFECTS: return #{u1, u2, ..., un}
	// Restituisce il numero di utenti registrati nella collezione
	public int getNUtenti();
	
	 
	// @EFFECTS: {u1, u2, ..., un} / {u1, u2, ..., un} 
	// Svuota la collezione 
	public void clearDb();
	
	
	class NotFoundUserException extends Exception {
		public NotFoundUserException() {
			super();
		}
		
		public NotFoundUserException(String s) {
	        super(s);
	    }
	}
		
	class UserAlreadyExistException extends Exception {
		public UserAlreadyExistException() {
	        super();
		}

		public UserAlreadyExistException(String s) {
	        super(s);
	    }
	}
		
	class LoginFailException extends Exception {
		public LoginFailException() {
	        super();
	    }

		public LoginFailException(String s) {
	        super(s);
		}
	}
	
}
 
	
	

