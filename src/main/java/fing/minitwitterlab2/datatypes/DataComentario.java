package fing.minitwitterlab2.datatypes;

import java.util.List;

public class DataComentario {
	
	private String id;
	private String texto;
	private String emailUsuario;
	private String comentarioPadre;
	private List<DataComentario> comentarios;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
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
	
	public String getComentarioPadre() {
		return comentarioPadre;
	}

	public void setComentarioPadre(String comentarioPadre) {
		this.comentarioPadre = comentarioPadre;
	}

	public List<DataComentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<DataComentario> comentarios) {
		this.comentarios = comentarios;
	}

	public DataComentario() {
		super();
	}

	public DataComentario(String id, String texto, String emailUsuario, String comentarioPadre,
			List<DataComentario> comentarios) {
		super();
		this.id = id;
		this.texto = texto;
		this.emailUsuario = emailUsuario;
		this.comentarioPadre = comentarioPadre;
		this.comentarios = comentarios;
	}

}

