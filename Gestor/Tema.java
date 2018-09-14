package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.Vector;

import javeriana.edu.co.Cliente;
import java.util.Vector;

class Tema {

    private Vector<Cliente> clientes;
    private String nombre;

    public Tema(String nombre) {
        this.nombre = nombre;
        clientes = new Vector<Cliente>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Vector<Cliente> getClientes() {
        return this.clientes;
    }

    public void setClientes(Vector<Cliente> clientes) {
        this.clientes = clientes;
    }

    public void addCliente(Cliente cliente){
        clientes.add(cliente);
    }

    public void deleteCliente(Cliente cliente){
        this.clientes.remove(cliente); 
    }
    
}
