/*
 * Federico Germinario 545081
 */

public class DataClass <E> {
	// Overview: tipo non modificabile che rappresenta un dato 
    // AF = <owner, E data> 
    // IR = owner != null && data != null
	
	private String owner;
	private final E data;              										  
	
	public DataClass(String owner, E data){
		if(owner == null || data == null) throw new NullPointerException();
		this.owner = owner;
		this.data = data;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public E getDato() {
		return data; 															
	}
	
	@Override
    public boolean equals(Object obj) {
		if(obj instanceof DataClass<?>) {
			if( ((DataClass<?>) obj).owner.equals(owner) &&
					((DataClass<?>) obj).data.equals(data)) {
				return true;
			}
		}
		return false;
	}
	
}
