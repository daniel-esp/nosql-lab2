package fing.minitwitterlab2.datatypes;

public class DataUsuario {

	private String id;
	private String email;
	
	public DataUsuario() {
		super();
	}
	
	public DataUsuario(String id, String email) {
		super();
		this.id = id;
		this.email = email;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
}
