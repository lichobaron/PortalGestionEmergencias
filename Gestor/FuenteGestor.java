package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import javeriana.edu.co.Mensaje;
import java.util.Vector;

class FuenteGestor {

    private InetAddress ip;
    private int puerto;
    private Vector<Mensaje> noticias;
    private String nombreUsuario;

    public FuenteGestor(InetAddress ip, int puerto, String nombreUsuario) {
        this.ip = ip;
        this.puerto = puerto;
        this.noticias = new Vector<Mensaje>();
        this.nombreUsuario = nombreUsuario;
    }

    public InetAddress getIp() {
        return this.ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPuerto() {
        return this.puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getNombreUsuario() {
        return this.nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void addNoticia(Mensaje m){
        noticias.add(m);
    }

    public void deleteNoticia(Mensaje m){
        this.noticias.remove(m); 
    }

    public String toString(){
        String string = null;
        string = String.format("Nombre Usuario: "+this.nombreUsuario+"\tIp: "+this.ip.toString()+"\tPuerto: "+this.puerto);
        return string;
    }
}
