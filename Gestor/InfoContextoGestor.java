package javeriana.edu.co;

import javeriana.edu.co.*;
import java.util.Vector;

class InfoContextoGestor{
    private String nombre;
    private Vector<ClienteGestor> clientes;

    InfoContextoGestor(String nombre){
        this.nombre = nombre;
        this.clientes = new Vector<ClienteGestor>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Vector<ClienteGestor> getClientes() {
        return this.clientes;
    }

    public void setClientes(Vector<ClienteGestor> clientes) {
        this.clientes = clientes;
    }

    public void addCliente(ClienteGestor cliente){
        this.clientes.add(cliente);
    }
}