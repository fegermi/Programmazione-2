/*
 * Federico Germinario 545081
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListSecureDataContainer<E> implements SecureDataContainer<E> {     
	// Overview: tipo modificabile che rappresenta una collezione di utenti con password e i loro dati di tipo E
    // AF = <ids, users>, <{ids.get(0), ..., ids.get(ids.size()-1)}, {users.get(0), ...,users.get(users.size()-1)}>
	//      con users.get(i) = {username-i, password-i, {dati-i.get(0), ..., dati-i.get(dati-i.size()-1} , 
	//		dati-i.get(j) = {owner-j, E data-j} per 0 <= i < users.size() e per 0 <= j < dati-i.size()
    // IR = ids != null && users != null && ids.size() = users.size() 
	//      && for all i. 0 <= i < ids.size() ==> (ids.get(i) != null && users.get(i) != null && users.get(i).username != null 
	//												&& users.get(i).password != null && users.get(i).dati != null
	//                                              && ids.get(i) = users.get(i).username 
	//											    && (for all j. 0 <= j < users.get(i).dati.size() ==> users.get(i).dati.get(j) != null &&
	//													  users.get(i).dati.get(j).owner != null && users.get(i).dati.get(j).data != null))		
	//		&& for all i,j. 0 <= i < j  < ids.size() ==> (ids.get(i) != ids.get(j))        
																			
	private List<String> ids;
	private List<User<E>> users;
	
	/*
	 * @EFFECTS: inizializza ids e users
	 */
	public ListSecureDataContainer() {
		ids = new ArrayList<>();
		users = new ArrayList<>();
	}
	
	
	/* @REQUIRES: Id != null && passw != null
	 * @MODIFIES: ids, users
	 * @THROWS: se Id = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Id) > 0 lancia una UserAlreadyExistException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: istanzia un nuovo oggetto User e lo associa all'Id corrispondente, aggiungendo Id in ids e l'oggetto Users 
	 *           in users nello stesso indice
	 */
	// Crea l’identità un nuovo utente della collezione
	public void createUser(String Id, String passw) throws UserAlreadyExistException {                          
		if(Id == null || passw == null) throw new NullPointerException();
		if(ids.indexOf(Id) > 0) throw new UserAlreadyExistException(Id);
		
		User<E> newUser = new User<E>(Id, passw);                             
		ids.add(Id);                                                         
		users.add(newUser);
	}
	
	
	/* @REQUIRES: Id != null && passw != null
	 * @MODIFIES: ids, users
	 * @THROWS: se Id = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Id) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Id)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)         
	 * @EFFECTS: recupera l'indice dell'arrayList contenente l'username Id e rimuove Id da ids e il corrispondente oggetto User da users
	 */
	// Elimina user e tutti i suoi dati                                                        
	public void removeUser(String Id, String passw) throws NotFoundUserException, LoginFailException {
		if(Id == null || passw == null) throw new NullPointerException();
		int i = ids.indexOf(Id);
		if(i == -1) throw new NotFoundUserException();
		if(users.get(i).login(Id, passw) == null) throw new LoginFailException(); 
		
		ids.remove(i);
		users.remove(i);
	}
	

	/* @REQUIRES: Id != null && oldPassw != null && newPassw != null
	 * @MODIFIES: users
	 * @THROWS: se Id = null || oldPassw = null || newPassw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Id) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Id)).password != oldPassw lancia una LoginFailException (eccezione non disponibile in Java, checked)         
	 * @EFFECTS: recupera l'oggetto User relativo a Id e modifica la sua password 
	 */
	// Cambia la password di un utente della collezione
	public void changePassword(String Id, String oldPassw, String newPassw) throws NotFoundUserException, LoginFailException {                
		if(Id == null || oldPassw == null || newPassw == null) throw new NullPointerException();
		int i = ids.indexOf(Id);
		if(i == -1) throw new NotFoundUserException();
		
		User<E> tmpUsr = users.get(i);
		if(!tmpUsr.setPassw(Id, oldPassw, newPassw)) throw new LoginFailException(Id);
	}
	
	
	/* @REQUIRES: Owner != null && passw != null
	 * @MODIFIES: - 
	 * @THROWS: se Owner = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Owner) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e restituisce il numero degli elementi prensenti in dati
	 */
	// Restituisce il numero degli elementi di un utente presenti nella collezione
	public int getSize(String Owner, String passw) throws NotFoundUserException, LoginFailException {                     
		if(Owner == null || passw == null) throw new NullPointerException();
		int i = ids.indexOf(Owner);
		if(i == -1) throw new NotFoundUserException();
		User<E> tmpUsr = users.get(i).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		return tmpUsr.getNElementi();
	}
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: users
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Owner) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e aggiunge data in dati. Se il dato è stato inserito correttamente ritorna true, altrimenti false  
	 */
	// Inserisce il valore del dato nella collezione se vengono rispettati i controlli di identità
	public boolean put(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		int i = ids.indexOf(Owner);
		if(i == -1) throw new NotFoundUserException();
		
		User<E> tmpUsr = users.get(i).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		//Creo una nuova istanza della classe DataClass con data e la aggiungo all'arrayList di Owner
		DataClass<E> tmpData = new DataClass<E>(Owner, data);
		return tmpUsr.addDato(tmpData);
	}
	
		
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: - 
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)          
	 * 			se ids.indexOf(Owner) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e restituisce una copia della prima occorrenza del dato data 
	 *           se l'utente Owner ne è in possesso, altrimenti null 
	 */
	// Ottiene una copia del valore del dato nella collezione se vengono rispettati i controlli di identità
	public E get(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {                                                               
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		int i = ids.indexOf(Owner);
		if(i == -1) throw new NotFoundUserException();
		
		User<E> tmpUsr = users.get(i).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
			
		return tmpUsr.getDato(data);      
	}
	
	
	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: users
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Owner) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner, se l'utente è in possesso di data, rimuovo tutte le occorenze di data in dati e restituisco data, altrimenti null
	 */
	// Rimuove il dato nella collezione se vengono rispettati i controlli di identità
	public E remove(String Owner, String passw, E data) throws NotFoundUserException, LoginFailException {                     
		if(Owner == null || passw == null || data == null) throw new NullPointerException();
		int i = ids.indexOf(Owner);
		if(i == -1) throw new NotFoundUserException();
		User<E> tmpUsr = users.get(i).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
	
		// Rimuove tutte le occorenze di data da Owner
		if(tmpUsr.removeDato(data)) {
			return data;
		}
		return null;
	}
	

	/* @REQUIRES: Owner != null && passw != null && data != null
	 * @MODIFIES: users
	 * @THROWS: se Owner = null || passw = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Owner) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
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
	 * @MODIFIES: users
	 * @THROWS: se Owner = null || passw = null || Other = null || data = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Owner) = -1 || ids.indexOf(Other) = -1  lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e l'oggetto User relativo a Owner , se l'utente Owner è in possesso di data ed è il proprietario, 
	 * 			 copio data nella collezione dei dati di Other, altrimenti stampo a video "Impossibile condividere"
	 */
	// Condivide il dato nella collezione con un altro utente se vengono rispettati i controlli di identità
	public void share(String Owner, String passw, String Other, E data) throws NotFoundUserException, LoginFailException {
		if(Owner == null || passw == null || Other == null || data == null) throw new NullPointerException();
		int i = ids.indexOf(Owner);
		if(i == -1) throw new NotFoundUserException(Owner);
		User<E> tmpUsr = users.get(i).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		//Recupero l'oggetto DataClass<E> che contiene data
		DataClass<E> tmpD = tmpUsr.getDataClass(data);
		
		int j = ids.indexOf(Other);
		if(j == -1) throw new NotFoundUserException(Other);
		
		// Controllo che Owner abbia il dato e che sia il proprietario
		if(tmpD != null && tmpD.getOwner().equals(Owner)) {
			DataClass<E> newTmpD = new DataClass<E>(Owner, data);
			User<E> usrOther = users.get(j);                                          
			usrOther.addDato(newTmpD);  
		}else {
			System.out.println("Impossibile condividere il dato con " + Other); 
		}
	}
	
	/* @REQUIRES: Owner != null && passw != null 
	 * @MODIFIES: -
	 * @THROWS: se Owner = null || passw = null lancia una NullPointerException (eccezione disponibile in Java, unchecked)
	 * 			se ids.indexOf(Owner) = -1 lancia una NotfoundUserException (eccezione non disponibile in Java, checked)
	 * 			se users.get(ids.indexOf(Owner)).password != passw lancia una LoginFailException (eccezione non disponibile in Java, checked)
	 * @EFFECTS: recupera l'oggetto User relativo a Owner e restituisce un iteratore senza remove che genera tutti i dati dell’User in ordine arbitrario
	 */
	// restituisce un iteratore (senza remove) che genera tutti i dati dell’utente in ordine arbitrario
	// se vengono rispettati i controlli di identità
	public Iterator<E> getIterator(String Owner, String passw) throws NotFoundUserException, LoginFailException{
		if(Owner == null || passw == null) throw new NullPointerException();
		int i = ids.indexOf(Owner);
		if(i == -1) throw new NotFoundUserException();
		User<E> tmpUsr = users.get(i).login(Owner, passw);
		if(tmpUsr == null) throw new LoginFailException(Owner); 
		
		return tmpUsr.getIterator();
	}

	/*
	 * @EFFECTS: return ids.size()
	 */
	public int getNUtenti() {
		return ids.size();
	}


	/*
	 * @EFFECTS: elimina tutti gli elementi da ids e da users
	 */
	public void clearDb() {
		ids.clear();
		users.clear();
	}

}
