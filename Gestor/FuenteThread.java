package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;
import java.util.Vector;

class FuenteThread extends Thread {

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente;
	private Gestor gestor;
	private int modo;

	public FuenteThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente, int modo){
		this.colaSubscripcionesFuente = colaSubscripcionesFuente;
		this.gestor = gestor;
		this.modo = modo;
	}

	public void setModo(int modo) {
		this.modo = modo;
	}

	public void run () {

		InetAddress ipFuente;
		int puertoFuente;
		Mensaje m;
		FuenteGestor fuenteGestor;

		while(true){
			if(colaSubscripcionesFuente.peek()!=null){
				for(Pair<InetAddress,Integer> b: gestor.getBackups()){
					Mensaje mc = colaSubscripcionesFuente.peek().getKey();
					mc.setIp(colaSubscripcionesFuente.peek().getValue().getKey());
					mc.setPuerto(colaSubscripcionesFuente.peek().getValue().getValue());
					this.sendMessage(mc, b.getKey(), b.getValue());
				}
				m = colaSubscripcionesFuente.peek().getKey();
				fuenteGestor = gestor.findFuente(m.getNombreUsuario());
				if(fuenteGestor==null){
					ipFuente = colaSubscripcionesFuente.peek().getValue().getKey();
					puertoFuente = colaSubscripcionesFuente.peek().getValue().getValue();
					FuenteGestor f = new FuenteGestor(ipFuente,puertoFuente,m.getNombreUsuario());
					gestor.addFuente(f);
					System.out.println("La fuente "+m.getNombreUsuario()+ " fue agregada.");							
				}
				else{
					fuenteGestor.setIp(colaSubscripcionesFuente.peek().getValue().getKey());
					fuenteGestor.setPuerto(colaSubscripcionesFuente.peek().getValue().getValue());
					System.out.println("La fuente "+m.getNombreUsuario()+ " ya existe.");
				}
				System.out.println("Subscripción fuente terminada!");
				this.gestor.saveState(this.gestor.getIpGestor(), this.gestor.getPuertoGestor());
				colaSubscripcionesFuente.remove();
			}
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