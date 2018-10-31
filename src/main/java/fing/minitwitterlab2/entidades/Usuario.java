package fing.minitwitterlab2.entidades;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import fing.minitwitterlab2.datatypes.DataUsuario;

public class Usuario {

	private ObjectId id;
	private String email;
	
	public Usuario() {
		super();
	}
	
	public Usuario(String email) {
		super();
		this.email = email;
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@BsonIgnore
	public DataUsuario getDatatype() {
		DataUsuario data = new DataUsuario();
		data.setEmail(this.email);
		if(this.id != null)
			data.setId(this.id.toString());
		return data;
	}
	
}
