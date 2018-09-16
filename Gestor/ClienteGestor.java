package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

class ClienteGestor {
    
    private InetAddress ip;
    private int puerto;
    private String nombreUsuario;

    ClienteGestor(InetAddress ip, int puerto, String nombreUsuario) {
        this.ip = ip;
        this.puerto = puerto;
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
    
    public String toString(){
        String string = null;
        string = String.format("Nombre Usuario: "+this.nombreUsuario+"\tIp: "+this.ip.toString()+"\tPuerto: "+this.puerto);
        return string;
    }
    
}
