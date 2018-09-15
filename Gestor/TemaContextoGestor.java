package javeriana.edu.co;

import javeriana.edu.co.*;
import java.util.Vector;

class TemaContextoGestor{
    private String nombre;
    private Vector<InfoContextoGestor> datos;

    TemaContextoGestor(String nombre){
        this.nombre = nombre;
        this.datos = new Vector<InfoContextoGestor>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Vector<InfoContextoGestor> getDatos() {
        return this.datos;
    }

    public void setDatos(Vector<InfoContextoGestor> datos) {
        this.datos = datos;
    }

    public void addInfoContexto(InfoContextoGestor infoContextoGestor){
        this.datos.add(infoContextoGestor);
    }

    public boolean existInfoContexto(String infoContexto){
		int i = 0;
		while(i<this.datos.size()){
			if(this.datos.get(i).getNombre().equals(infoContexto)){
				return true;
			}
			i++;
		}
		return false;
    }
    
    public InfoContextoGestor findInfoContexto(String infoContexto){
		int i = 0;
		while(i<this.datos.size()){
			if(this.datos.get(i).getNombre().equals(infoContexto)){
				return this.datos.get(i);
			}
			i++;
		}
		return null;
	}
}