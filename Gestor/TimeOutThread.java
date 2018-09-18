package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;
import java.util.Vector;

class TimeOutThread extends Thread {

	private Gestor gestor;
    private int modo;
    private InetAddress ip;
    private int port;

	public TimeOutThread(Gestor gestor, int modo, InetAddress ip, int port){
		this.gestor = gestor;
        this.modo = modo;
        this.ip = ip;
        this.port =port;
	}

	public void setModo(int modo) {
		this.modo = modo;
	}

	public void run () {
        if(gestor.getBackups().size()>=1){
            while(true){
                try {
                    if(modo==1){
                        sendMessage(new Mensaje(Mensaje.Tipo.ERROR,"",""), gestor.getBackups().get(0).getKey(), gestor.getBackups().get(0).getValue());
                        sleep(5000);
                    }                   
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        else{
            System.out.println("No hay nodos de respaldo!!\n");
        }
    }
    
	private void sendMessage(Mensaje mensaje, InetAddress IPAddress, int port){
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            ByteArrayOutputStream bStream  = new ByteArrayOutputStream();
            ObjectOutput sendData = new ObjectOutputStream(bStream);
            sendData.writeObject(mensaje);
            sendData.close();
            byte[] serializedMessage = bStream.toByteArray();;
            DatagramPacket sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, port);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}