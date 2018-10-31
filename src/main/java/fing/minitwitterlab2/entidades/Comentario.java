package fing.minitwitterlab2.entidades;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import fing.minitwitterlab2.datatypes.DataComentario;

public class Comentario {
	
	private ObjectId id;
	private String texto;
	private String emailUsuario;
	private ObjectId comentarioPadre;

	public Comentario() {
		super();
	}
	
	public Comentario(ObjectId id, String texto, String emailUsuario, ObjectId comentarioPadre) {
		super();
		this.id = id;
		this.texto = texto;
		this.emailUsuario = emailUsuario;
		this.comentarioPadre = comentarioPadre;
	}
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public String getEmailUsuario() {
		return emailUsuario;
	}
	
	public void setEmailUsuario(String emailUsuario) {
		this.emailUsuario = emailUsuario;
	}
	
	public ObjectId getComentarioPadre() {
		return comentarioPadre;
	}

	public void setComentarioPadre(ObjectId comentarioPadre) {
		this.comentarioPadre = comentarioPadre;
	}

	@BsonIgnore
	public DataComentario getDatatype() {
		DataComentario data = new DataComentario();
		data.setTexto(this.texto);
		data.setEmailUsuario(this.emailUsuario);
		if(this.id != null)
			data.setId(this.id.toString());
		if(this.comentarioPadre != null)
			data.setComentarioPadre(this.comentarioPadre.toString());
		return data;
	}

}

