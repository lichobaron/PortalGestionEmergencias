package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import javeriana.edu.co.Mensaje;
import java.util.Vector;

class Fuente {

    private InetAddress ip;
    private int puerto;
    private Vector<Mensaje> noticias;

    public Fuente(InetAddress ip, int puerto) {
        this.ip = ip;
        this.puerto = puerto;
        this.noticias = new Vector<Mensaje>();
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

    public void addNoticia(Mensaje m){
        noticias.add(m);
    }

    public void deleteNoticia(Mensaje m){
        this.noticias.remove(m); 
    }
}
