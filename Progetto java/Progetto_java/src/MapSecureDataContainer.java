/*
 * Federico Germinario 545081
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapSecureDataContainer<E> implements SecureDataContainer<E> {   
	// Overview: tipo modificabile che rappresenta una collezione di utenti con password e i loro dati di tipo E
	// AF = db: K -> V tale che db(keys.get(i)) = values.get(i) per 0 <= i < keys.size() con 
	//		keys = {username-0, ...,username-keys.size()-1> , 
	//      values = {User.get(0), ..., User.get(keys.size()-1)},
	//      User.get(i) = {username-i, password-i, {dati-i.get(0), ..., dati-i.get(dati.size()-1}, 
	//		dati-i.get(j) = {owner-j, E data-j} per 0 <= i < values.size() e per 0 <= j < dati-i.size()
	//
    // IR = db != null keys != null && values != null && keys.size() = values.size()
    //      && for all i. 0 <= i < keys.size() ==> (keys.get(i) != null && values.get(i) != null && values.get(i).User.username != null 
    // 											      && values.get(i).User.password != null && values.get(i).User.dati != null 
    //                                                && keys.get(i) = values.get(i).username
    //                                                && (for all j. 0 <= j < values.get(i).dati.size() ==> values.get(i).dati.get(j) != null 
    //                                                      && values.get(i).dati.get(j).owner != null && values.get(i).dati.get(j).data != null))
	//      && for all i,j. 0 <= i < j  < keys.size() ==> (keys.get(i) != keys.get(j)) 
	 																		
	private Map<String, User<E>> db;
	
	/*
	 * @EFFECTS: inizializza la HashMap db
	 */
	public MapSecureDataContainer() {
		db = new HashMap<>();
	}
	
	
	/* @REQUIRES: Id != null && passw != null
	 * @MODIFIES: db
	 * @THROWS: se Id = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Id) = true lancia una UserAlreadyExistException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: associa Id a un istanzia di nuovo oggetto User, db.put(Id, new User<E>(Id, passw)) 
	 */
	// Crea l’identità un nuovo utente della collezione
	public void createUser(String Id, String passw) throws UserAlreadyExistException {                          
		if(Id == null || passw == null) throw new NullPointerException();
		if(db.containsKey(Id)) throw new UserAlreadyExistException(Id);
		
		User<E> newUser = new User<E>(Id, passw);                                                                                
		db.put(Id, newUser);
	}
	
	
	/* @REQUIRES: Id != null && passw != null
	 * @MODIFIES: db
	 * @THROWS: se Id = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Id) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Id).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)     
	 * @EFFECTS: rimuove l'associazione tra l' Id e il relativo oggetto User 
	 */
	//Elimina un utente dalla collezione                                                     
	public void removeUser(String Id, String passw) throws NotFoundUserException, LoginFailException {
		if(Id == null || passw == null) throw new NullPointerException();
		if(!db.containsKey(Id)) throw new NotFoundUserException(Id);
		if(db.get(Id).login(Id, passw) == null) throw new LoginFailException(Id); 
		
		db.remove(Id);
	}
	
	
	/* @REQUIRES: Id != null && oldPassw != null && newPassw != null
	 * @MODIFIES: db
	 * @THROWS: se Id = null || oldPassw = null || newPassw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Id) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Id).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)  
	 * @EFFECTS: recupera l'oggetto User relativo a Id e modifica la sua password, db(Id).oldPassw = newPassw
	 */
	//Cambia la password a un utente delle collezione
	public void changePassword(String Id, String oldPassw, String newPassw) throws NotFoundUserException, LoginFailException {   
		if(Id == null || oldPassw == null || newPassw == null) throw new NullPointerException();
		if(!db.containsKey(Id)) throw new NotFoundUserException(Id);
		
		User<E> tmpUsr = db.get(Id);
		if(!tmpUsr.setPassw(Id, oldPassw, newPassw)) throw new LoginFailException(Id);
	}
	
	
	/* @REQUIRES: Owner != null && passw != null
	 * @MODIFIES: - 
	 * @THROWS: se Owner = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Owner) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e restituisce il numero degli elementi prensenti in dati, db(Owner).dati.size() 
	 */
	// Restituisce il numero degli elementi di un utente presenti nella collezione
	public int getSize(String Owner, String passw) throws NotFoundUserException, LoginFailException {                      
		if(Owner == null || passw == null) throw new NullPointerException();
		if(!db.containsKey(Owner)) throw new NotFoundUserException(Owner);
		User<E> tmpUsr = db.get(Owner).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		return tmpUsr.getNElementi();
	}
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: db
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Owner) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e aggiunge data in dati. Se il dato è stato inserito correttamente ritorna true, altrimenti false 
	 */
	// Inserisce il valore del dato nella collezione se vengono rispettati i controlli di identità
	public boolean put(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		if(!db.containsKey(Owner)) throw new NotFoundUserException(Owner);
		
		User<E> tmpUsr = db.get(Owner).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		//Creo una nuova instanza della classe DataClass con data e la aggiungo all'arrayList di Owner
		DataClass<E> tmpData = new DataClass<E>(Owner, data);           
		return tmpUsr.addDato(tmpData);
	}
	
		
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: - 
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)           
	 * 			se db.containsKey(Owner) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: restituisce una copia della prima occorrenza del dato data se l'utente Owner ne è in possesso, altrimenti null 
	 * 			 recupera l'oggetto User relativo a Owner e restituisce una copia del dato data se l'utente Owner ne è in possesso, altrimenti null  
	 */
	// Ottiene una copia del valore del dato nella collezione se vengono rispettati i controlli di identità
	public E get(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {                                                             
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		if(!db.containsKey(Owner)) throw new NotFoundUserException(Owner);
		
		User<E> tmpUsr = db.get(Owner).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		return tmpUsr.getDato(data);                                                                                                                                  
	}
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: db
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Owner) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner, se l'utente è in possesso di data, rimuovo tutte le occorenze 
	 *           di data in dati e restituisco data, altrimenti null
	 */
	// Rimuove il dato nella collezione se vengono rispettati i controlli di identità
	public E remove(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {                      
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		if(!db.containsKey(Owner)) throw new NotFoundUserException(Owner);
		
		User<E> tmpUsr = db.get(Owner).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(); 
		
		// Rimuove tutte le occorenze di data da Owner
		if(tmpUsr.removeDato(data)) {
			return data;
		}
		return null;
	}
	

	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: db(Owner)
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Owner) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: se Owner possiede data crea una copia di quest'ultimo nella sua collezione di dati, altrimenti lancia NullPointerException 
	 */
	// Crea una copia del dato nella collezione se vengono rispettati i controlli di identità
	public void copy(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		
		// Ottengo una copia di data e la aggiungo alla struttura dati di Owner
		E d = get(Owner, passw, data);
		put(Owner, passw, d);
	}
	
		
	/* @REQUIRES: Owner != null && passw != null && Other != null && data != null
	 * @MODIFIES: db
	 * @THROWS: se Owner = null || passw = null || Other = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Owner) = false || se db.containsKey(Other) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw  lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e l'oggetto User relativo a Owner , se l'utente Owner è in possesso di data ed è il proprietario, 
	 * 			 copio data nella collezione dei dati di Other, altrimenti stampo a video "Impossibile condividere il dato"
	 */
	// Condivide il dato nella collezione con un altro utente se vengono rispettati i controlli di identità
	public void share(String Owner, String passw, String Other, E data) throws NotFoundUserException, LoginFailException {
		if(Owner == null || passw == null || Other == null || data == null) throw new NullPointerException();
		if(!db.containsKey(Owner)) throw new NotFoundUserException(Owner);
		if(!db.containsKey(Other)) throw new NotFoundUserException(Other);
		
		User<E> tmpUsr = db.get(Owner).login(Owner, passw);  
		if(tmpUsr == null) throw new LoginFailException(); 
		
		// Recupero l'oggetto DataClass<E> che contiene data
		DataClass<E> tmpD = tmpUsr.getDataClass(data);
		
		// Controllo che Owner abbia il dato e che sia il proprietario
		if(tmpD != null && tmpD.getOwner().equals(Owner)) {
			DataClass<E> newTmpD = new DataClass<E>(Owner, data);
			User<E> usrOther = db.get(Other);
			usrOther.addDato(newTmpD); 
		}else {
			System.out.println("Impossibile condividere il dato con " + Other); 
		}
	}
	
	
	/* @REQUIRES: Owner != null && passw != null 
	 * @MODIFIES: -
	 * @THROWS: se Owner = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se db.containsKey(Owner) = false lancia una NotFoundUserException (eccezione non disponibile in Java, checked)
	 * 			se db(Owner).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e restituisce un iteratore senza remove che genera tutti i dati dell’User in ordine arbitrario
	 */
	// Restituisce un iteratore (senza remove) che genera tutti i dati dell’utente in ordine arbitrario
	// se vengono rispettati i controlli di identità
	public Iterator<E> getIterator(String Owner, String passw) throws NotFoundUserException, LoginFailException{
		if(Owner == null || passw == null) throw new NullPointerException();
		if(!db.containsKey(Owner)) throw new NotFoundUserException(Owner);
		
		User<E> tmpUsr = db.get(Owner).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(); 
		
		return tmpUsr.getIterator();
	}
	
	
	/*
	 * @EFFECTS: ritorna keys.size()
	 */
	public int getNUtenti() {
		return db.size();
	}
	
	
	/*
	 * @EFFECTS: pulisce db
	 */
	public void clearDb() {
		db.clear();
	}

}
