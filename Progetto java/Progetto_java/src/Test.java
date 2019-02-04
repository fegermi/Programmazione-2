/* Federico Germinario 545081
 * 
 * BATTERIA DI TEST PER IL PROGETTO
 * 
 */

import java.util.Iterator;

public class Test{
    
	public static class Class {
    	public int num1;
    	public int num2;
    	public String string;

        public Class(int a, int b, String c) {
            num1 = a;
            num2 = b;
            string = c;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Class)) throw new ClassCastException();
            Class o = (Class) other;
            return (o.num1 == this.num1 && o.num2 == this.num2 && o.string.equals(this.string)); 
        }
    }
    
    public static void testClass(SecureDataContainer<Class> c) {
    	System.out.println("[TestClass]");

        String FEDERICO_PASSW = "F";
        String MANUELA_PASSW = "M";
        String STEFANO_PASSW = "S";
        
        // Creo 3 utenti 
        try {
        	c.createUser("Federico", FEDERICO_PASSW);
        	c.createUser("Manuela", MANUELA_PASSW);
        	c.createUser("Stefano", STEFANO_PASSW);
        	System.out.println("\t[TestClass-1] Superato!");
        }catch(NullPointerException | SecureDataContainer.UserAlreadyExistException e){
			System.out.println("[TestClass-1] Fallito!\n");
			return;
		}
        
        try {
        	// Lancia UserAlreadyExistException poich� l'username Manuela � gia registrato
        	c.createUser("Manuela", "passw");
        	System.out.println("[TestClass-2] Fallito!\n");
        	return;
        }catch(NullPointerException e){
			System.out.println("[TestClass-2] Fallito!\n");
			return;
		}
		catch(SecureDataContainer.UserAlreadyExistException e){
			System.out.println("\t[TestClass-2] Superato!");
		}
        
        assert(c.getNUtenti() == 3); 
        
        Class class1 = new Class(1, 2, "a");
        Class class2 = new Class(3, 4, "b");
        Class class3 = new Class(5, 6, "c");
        
        // Inserisco alcuni dati nei relativi utenti 
        try {
        	c.put("Federico", FEDERICO_PASSW, class1);
        	c.put("Federico", FEDERICO_PASSW, class2);
        	c.put("Federico", FEDERICO_PASSW, class3);
        	c.put("Manuela", MANUELA_PASSW, class1);
        	System.out.println("\t[TestClass-3] Superato!");
        }catch(NullPointerException | SecureDataContainer.NotFoundUserException | SecureDataContainer.LoginFailException e ){
			System.out.println("[TestClass-3] Fallito!\n");
			return;
        }
	
        try {
        	// Lancia NotFoundUserException poich� l'username Giacomo non � registrato
        	c.put("Giacomo", MANUELA_PASSW, class2);
        	System.out.println("[TestClass-4] Fallito!");
			return;
        }catch(NullPointerException e){
			System.out.println("[TestClass-4] Fallito!\n");
			return;
        }catch(SecureDataContainer.NotFoundUserException e){
        	System.out.println("\t[TestClass-4] Superato!");
        }
		catch(SecureDataContainer.LoginFailException e){
			System.out.println("[TestClass-4] Fallito!\n");
			return;
		}
        
        String STEFANO_NEWPASSW = "ST";
        Class get;
        try {
        	// Cambio password all'username Stefano 
        	c.changePassword("Stefano", STEFANO_PASSW, STEFANO_NEWPASSW);
        	get = c.get("Federico", FEDERICO_PASSW, new Class(1, 2, "ciao"));
        	assert(get == null);
        	get = c.get("Federico", FEDERICO_PASSW, new Class(1, 2, "a"));
        	assert(get != null);
        	System.out.println("\t[TestClass-5] Superato!");
        }catch(NullPointerException | SecureDataContainer.NotFoundUserException | SecureDataContainer.LoginFailException e ){
			System.out.println("[TestClass-5] Fallito!\n");
			return;
        }
	
        
        try {
        	// lancia LoginFailException poich� la password di Stefano � stata modificata
        	c.put("Stefano", STEFANO_PASSW, class1);   
        	System.out.println("[TestClass-6] Fallito!\n");
        	return;
        }catch(NullPointerException | SecureDataContainer.NotFoundUserException e){
			System.out.println("[TestClass-6] Fallito\n!");
			return;
        }catch(SecureDataContainer.LoginFailException e){
			System.out.println("\t[TestClass-6] Superato!");
		}
       
        // Pulisco la struttura dati 
        c.clearDb();
 
        System.out.println("[TestClass completato]\n");
    }
    
    public static void testString(SecureDataContainer<String> c) {
    	System.out.println("[TestString]");

        String FEDERICO_PASSW = "F";
        String MANUELA_PASSW = "M";
        String STEFANO_PASSW = "S";
        
        // Creo 3 utenti 
        try {
        	c.createUser("Federico", FEDERICO_PASSW);
        	c.createUser("Manuela", MANUELA_PASSW);
        	c.createUser("Stefano", STEFANO_PASSW);
        	System.out.println("\t[TestString-1] Superato!");
        }catch(NullPointerException | SecureDataContainer.UserAlreadyExistException e){
        	System.out.println("[TestString-1] Fallito!\n");
        	return;
        }
        
        assert(c.getNUtenti() == 3);   
        
        // Inserisco dei dati in Federico 
        try {
        	c.put("Federico", FEDERICO_PASSW, "dato1_f");
        	c.put("Federico", FEDERICO_PASSW, "dato2_f");
        	System.out.println("\t[TestString-2] Superato!");
        }catch(NullPointerException | SecureDataContainer.NotFoundUserException | SecureDataContainer.LoginFailException e ){
			System.out.println("[TestString-2] Fallito!\n");
			return;
        }
        	
        try {
        	// Lancia LoginFailException poich� la password � errata
        	c.put("Federico", "f", "dato3_f");
        	System.out.println("[TestString-3] Fallito!");
        	return;
        }catch(NullPointerException  | SecureDataContainer.NotFoundUserException e){
			System.out.println("[TestString-3] Fallito!n");
			return;
        }catch(SecureDataContainer.LoginFailException e){
			System.out.println("\t[TestString-3] Superato!\n");
		}
        
 
        try {
        	// Inserisco dei dati in manuela e in federico 
        	c.put("Manuela", MANUELA_PASSW, "dato1_m");
        	c.put("Manuela", MANUELA_PASSW, "dato2_m");
        
        	c.put("Stefano", STEFANO_PASSW, "dato1_s");
        	c.put("Stefano", STEFANO_PASSW, "dato2_s");
        	
        	// Condivido alcuni dati 
        	c.share("Federico", FEDERICO_PASSW, "Manuela", "dato1_f");
        	c.share("Federico", FEDERICO_PASSW, "Stefano", "dato1_f");
        	c.share("Stefano", STEFANO_PASSW, "Federico", "dato2_s");
        	c.copy("Manuela", MANUELA_PASSW, "dato1_m");
        	
        	// Verifico che Federico a 3 dati
        	assert(c.getSize("Federico", FEDERICO_PASSW) == 3);
        	
        	
        	// Stampo i dati di tutti gli utenti registrati
        	Iterator<String> itF = c.getIterator("Federico", FEDERICO_PASSW);
        	System.out.println("\tFederico:");
            while (itF.hasNext()) {
                String s = (String) itF.next();
                System.out.println("\t" + s);
            }
            System.out.println();
            
            Iterator<String> itM = c.getIterator("Manuela", MANUELA_PASSW);
        	System.out.println("\tManuela:");
        	while (itM.hasNext()) {
        		String s = (String) itM.next();
        		System.out.println("\t" + s);
        	}
        	System.out.println();
        	
        	Iterator<String> itS = c.getIterator("Stefano", STEFANO_PASSW);
        	System.out.println("\tStefano:");
        	while (itS.hasNext()) {
        		String s = (String) itS.next();
        		System.out.println("\t" + s);
        	}
        	System.out.println();
        	
        	//Rimuovo l'utente Federico 
        	c.removeUser("Federico", FEDERICO_PASSW);
            
        	System.out.println("\t[TestString-4] Superato!");
        	
        }catch(NullPointerException | SecureDataContainer.NotFoundUserException | SecureDataContainer.LoginFailException e ){
			System.out.println("[TestString-4] Fallito!\n");
			return;
        }
        
        try {
        	//Lancia NullPointerException poich� il dato dato3_m non � in posseso di Manuela  
			c.copy("Manuela", MANUELA_PASSW, "dato3_m");
			System.out.println("[TestString-5] Fallito!");
			return;	
        }catch(NullPointerException e){
			System.out.println("\t[TestString-5] Superato!");
        }catch (SecureDataContainer.NotFoundUserException | SecureDataContainer.LoginFailException e1 ) {
        	System.out.println("[TestString-5] Fallito!\n");
			return;	
        }
		
        
        try {
        	// Lancia NotFoundUserException poich� l'utente Federico � stato eliminato
        	Iterator<String> itF = c.getIterator("Federico", FEDERICO_PASSW);
        	System.out.println("[TestString-6] Fallito!");
			return;   	
        }catch(NullPointerException e){
			System.out.println("[TestString-6] Fallito!");
			return;
        }catch(SecureDataContainer.NotFoundUserException e){
        	System.out.println("\t[TestString-6] Superato!");
        }
		catch(SecureDataContainer.LoginFailException e){
			System.out.println("[TestString-6] Fallito!");
			return;
		}
        
        try {
        	assert(c.getSize("Manuela", MANUELA_PASSW) == 4 );
        	
        	// "dato_m" occorre 2 volte in Manuela
			c.remove("Manuela", MANUELA_PASSW, "dato1_m");
			assert(c.getSize("Manuela", MANUELA_PASSW) == 2 );
			System.out.println("\t[TestString-7] Superato!");
        }catch(NullPointerException | SecureDataContainer.NotFoundUserException | SecureDataContainer.LoginFailException e ){
			System.out.println("[TestString-7] Fallito!\n");
			return;
        }
                
        //Pulisco la struttura dati 
        c.clearDb();
        
        System.out.println("[TestString Completato]");
        
        }
    
    public static void main(String[] args) {
    	System.out.println("----------------------------------------");
    	System.out.println("TEST MapSecureDataContainer\n");
        SecureDataContainer<Class> test1 = new MapSecureDataContainer<>();
        testClass(test1);
        
        SecureDataContainer<String> test2 = new MapSecureDataContainer<>();
        testString(test2);
        
        System.out.println("----------------------------------------");
        System.out.println("TEST ListSecureDataContainer\n");
        SecureDataContainer<Class> test3 = new ListSecureDataContainer<>();
        testClass(test3);
        
        SecureDataContainer<String> test4 = new ListSecureDataContainer<>();
        testString(test4);
    }
}


