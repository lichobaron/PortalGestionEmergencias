package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

class Cliente {
    
    private InetAddress ip;
    private int puerto;

    Cliente(InetAddress ip, int puerto) {
        this.ip = ip;
        this.puerto = puerto;
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
    
    
}
