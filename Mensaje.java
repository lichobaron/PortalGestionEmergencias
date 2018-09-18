package javeriana.edu.co;

import java.util.List;
import java.util.Vector;
import java.io.Serializable;
import java.net.InetAddress;

class Mensaje implements Serializable {
	public static enum Tipo {
		SUBSCLIE, SUBSFUEN, NOTICI, UPT, ERROR, TEMAS
	}

	private Tipo tipo;
	private List<String> temas;
	private List<String> infoContext;
	private String cuerpo;
	private String nombreUsuario;
	private InetAddress ip;
	private Integer puerto;

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

	Mensaje(Tipo tipo) {
		this.tipo = tipo;
		this.temas = new Vector<String>();
		this.infoContext = new Vector<String>();
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

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public Integer getPuerto() {
		return puerto;
	}

	public void setPuerto(Integer puerto) {
		this.puerto = puerto;
	}

	public String toString() {
		String string = null;

		switch(this.tipo) {
			case SUBSCLIE:
				string = String.format("tipo: suscripcion cliente; nombreUsuario: %s; tema: %s; " +
					"%s cuerpo: %s", this.nombreUsuario, this.temas.get(0), this.infoContext.get(0), this.cuerpo);
				break;

			case SUBSFUEN:
				string = String.format("tipo: suscripcion fuente; nombreUsuario: %s; " +
					"cuerpo: %s", this.nombreUsuario, this.cuerpo);
				break;

			case NOTICI:
				string = String.format("tipo: suscripcion noticia; tema: %s; " +
					"cuerpo: %s", this.temas.get(0), this.cuerpo);
				break;
			case TEMAS:
				string = String.format("tipo: temas disponibles; tema: %s; " +
				"cuerpo: %s", this.temas.toString(), this.cuerpo);
				break;
			case ERROR:
				string = String.format("tipo:  error; " +
				"cuerpo: %s", this.cuerpo);
				break;
		}

		return string;
	}
}