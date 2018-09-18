package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.Vector;

import javeriana.edu.co.ClienteGestor;
import java.util.Vector;
import java.io.Serializable;

class TemaGestor implements Serializable{

    private Vector<ClienteGestor> clientes;
    private String nombre;

    public TemaGestor(String nombre) {
        this.nombre = nombre;
        clientes = new Vector<ClienteGestor>();
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
        clientes.add(cliente);
    }

    public void deleteCliente(ClienteGestor cliente){
        this.clientes.remove(cliente); 
    }

    public String toString(){
        String string = null;
        string = String.format("Nombre Tema: "+this.nombre+"\nClientes:\n");
        for(ClienteGestor c: clientes){
           string += c.toString() + "\n";
        }
        return string;
    }
    
}
