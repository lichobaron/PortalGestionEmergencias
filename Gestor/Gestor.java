package javeriana.edu.co;

import javeriana.edu.co.*;
import java.util.Vector;


class Gestor {

	private Vector<ClienteGestor> clientes;
	private Vector<TemaGestor> temas;
	private Vector<TemaContextoGestor> infoContexto;
	private Vector<FuenteGestor> fuentes;

	Gestor(){
		this.clientes = new Vector<ClienteGestor>();
		this.temas = new Vector<TemaGestor>();
		this.temas.add(new TemaGestor("Inundaciones"));
		this.temas.add(new TemaGestor("Incendios"));
		this.temas.add(new TemaGestor("Sismos"));
		this.temas.add(new TemaGestor("Lluvias"));
		this.temas.add(new TemaGestor("Avalanchas"));
		this.fuentes = new Vector<FuenteGestor>();
		this.infoContexto = new Vector<TemaContextoGestor>();
		this.infoContexto.add(new TemaContextoGestor("edad"));
		this.infoContexto.add(new TemaContextoGestor("residencia"));
		this.infoContexto.add(new TemaContextoGestor("genero"));
		this.infoContexto.add(new TemaContextoGestor("grupo etnico"));
		this.findInfoContexto("residencia").addInfoContexto(new InfoContextoGestor("cachipay"));
	}
	
	public Vector<ClienteGestor> getClientes() {
		return this.clientes;
	}

	public Vector<FuenteGestor> getFuentes() {
		return this.fuentes;
	}

	public Vector<TemaGestor> getTemas() {
		return this.temas;
	}

	public Vector<TemaContextoGestor> getInfoContexto() {
		return this.infoContexto;
	}

	public void setClientes(Vector<ClienteGestor> clientes) {
		this.clientes = clientes;
	}

	public void setFuentes(Vector<FuenteGestor> fuentes) {
		this.fuentes = fuentes;
	}

	public void setTemas(Vector<TemaGestor> temas) {
		this.temas = temas;
	}

	public void setInfoContexto(Vector<TemaContextoGestor> infoContexto) {
		this.infoContexto = infoContexto;
	}

	public void addCliente(ClienteGestor cliente){
		this.clientes.add(cliente);
	}

	public void addFuente(FuenteGestor fuente){
		this.fuentes.add(fuente);
	}

	public void addTema(TemaGestor tema){
		this.temas.add(tema);
	}

	public boolean existCliente(String username){
		int i = 0;
		while(i<this.clientes.size()){
			if(this.clientes.get(i).getNombreUsuario().equals(username)){
				return true;
			}
			i++;
		}
		return false;
	}

	public ClienteGestor findCliente(String username){
		int i = 0;
		while(i<this.clientes.size()){
			if(this.clientes.get(i).getNombreUsuario().equals(username)){
				return this.clientes.get(i);
			}
			i++;
		}
		return null;
	}

	public boolean existFuente(String fuente){
		int i = 0;
		while(i<this.fuentes.size()){
			if(this.fuentes.get(i).getNombreUsuario().equals(fuente)){
				return true;
			}
			i++;
		}
		return false;
	}

	public FuenteGestor findFuente(String fuente){
		int i = 0;
		while(i<this.fuentes.size()){
			if(this.fuentes.get(i).getNombreUsuario().equals(fuente)){
				return this.fuentes.get(i);
			}
			i++;
		}
		return null;
	}

	public boolean existTema(String tema){
		int i = 0;
		while(i<this.temas.size()){
			if(this.temas.get(i).getNombre().equals(tema)){
				return true;
			}
			i++;
		}
		return false;
	}

	public TemaGestor findTema(String tema){
		int i = 0;
		while(i<this.temas.size()){
			if(this.temas.get(i).getNombre().equals(tema)){
				return this.temas.get(i);
			}
			i++;
		}
		return null;
	}

	public boolean existInfoContexto(String infoContexto){
		int i = 0;
		while(i<this.infoContexto.size()){
			if(this.infoContexto.get(i).getNombre().equals(infoContexto)){
				return true;
			}
			i++;
		}
		return false;
    }
    
    public TemaContextoGestor findInfoContexto(String infoContexto){
		int i = 0;
		while(i<this.infoContexto.size()){
			if(this.infoContexto.get(i).getNombre().equals(infoContexto)){
				return this.infoContexto.get(i);
			}
			i++;
		}
		return null;
	}
}