package javeriana.edu.co;

import java.util.List;
import java.util.Vector;
import java.io.Serializable;

class Mensaje implements Serializable {
	public static enum Tipo {
		SUBSCLIE, SUBSFUEN, NOTICI, UPT, ERROR, TEMAS
	}

	private Tipo tipo;
	private List<String> temas;
	private List<String> infoContext;
	private String cuerpo;
	private String nombreUsuario;

	Mensaje(Tipo tipo, String cuerpo, String nombreUsuario) {
		this.tipo = tipo;
		this.temas = new Vector<String>();
		this.infoContext = new Vector<String>();
		this.cuerpo = cuerpo;
		this.nombreUsuario = nombreUsuario;
	}

	Mensaje(Tipo tipo, String cuerpo) {
		this.tipo = tipo;
		this.temas = null;
		this.infoContext = null;
		this.cuerpo = cuerpo;
		this.nombreUsuario = null;
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

	public List<String> getInfoContext() {
		return infoContext;
	}

	public void setInfoContext(List<String> infoContext) {
		this.infoContext = infoContext;
	}

	public void addTema(String tema) {
		this.temas.add(tema);
	}

	public void addInfoContext(String infoContext) {
		this.infoContext.add(infoContext);
	}

	public String toString() {
		String string = null;

		switch(this.tipo) {
			case SUBSCLIE:
				string = String.format("tipo: subscricion cliente; nombreUsuario: %s; tema: %s; " +
					"%s cuerpo: %s", this.nombreUsuario, this.temas.get(0), this.infoContext.get(0), this.cuerpo);
				break;

			case SUBSFUEN:
				string = String.format("tipo: subscricion fuente; nombreUsuario: %s; " +
					"cuerpo: %s", this.nombreUsuario, this.cuerpo);
				break;

			case NOTICI:
				string = String.format("tipo: noticia; tema: %s; infoContext: %s;" +
					"Usuario: %s cuerpo: %s", this.temas, this.infoContext, this.nombreUsuario, this.cuerpo);
				break;

			case UPT:
				string = "tipo: UPT";
				break;

			case ERROR:
				string = String.format("tipo: Error; cuerpo: %s", this.cuerpo);
				break;
		}

		return string;
	}
}