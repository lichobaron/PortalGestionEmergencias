package javeriana.edu.co;

import java.util.List;
import java.util.Vector;
import java.io.Serializable;

class Mensaje implements Serializable {
	public static enum Tipo {
		SUBSCLIE, SUBSFUEN, NOTICI
	}

	private Tipo tipo;
	private List<String> temas;
	private String cuerpo;
	private String nombreUsuario;

	Mensaje(Tipo tipo, String cuerpo, String nombreUsuario) {
		this.tipo = tipo;
		this.temas = new Vector<String>();
		this.cuerpo = cuerpo;
		this.nombreUsuario = nombreUsuario;
	}

	public Tipo getTipo() {
		return this.tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public List<String> getTemas() {
		return this.temas;
	}

	public void setTemas(List<String> temas) {
		this.temas = temas;
	}

	public String getCuerpo() {
		return this.cuerpo;
	}

	public void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public void addTema(String tema) {
		this.temas.add(tema);
	}

	public String toString() {
		String string = null;

		switch(this.tipo) {
			case SUBSCLIE:
				string = String.format("tipo: subscricion cliente; nombreUsuario: %s; tema: %s; " +
					"cuerpo: %s", this.nombreUsuario, this.temas.get(0), this.cuerpo);
				break;

			case SUBSFUEN:
				string = String.format("tipo: subscricion fuente; nombreUsuario: %s; tema: %s; " +
					"cuerpo: %s", this.nombreUsuario, this.temas.get(0), this.cuerpo);
				break;

			case NOTICI:
				string = String.format("tipo: subscricion noticia; tema: %s; " +
					"cuerpo: %s", this.temas.get(0), this.cuerpo);
				break;
		}

		return string;
	}
}