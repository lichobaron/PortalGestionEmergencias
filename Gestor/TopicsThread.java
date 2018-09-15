package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class TopicsThread extends Thread {

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnvioTemasDisponibles;
    private Gestor gestor;
    private DatagramSocket serverSocket;

	public TopicsThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnvioTemasDisponibles, DatagramSocket serverSocket){
		this.colaEnvioTemasDisponibles = colaEnvioTemasDisponibles;
        this.gestor = gestor;
        this.serverSocket = serverSocket;
	}

	public void run () {

		InetAddress ipFuente;
		int puertoFuente;
		Mensaje m;
        ClienteGestor clienteGestor;
        DatagramSocket clientSocket;
        InetAddress IPAddress;
        ByteArrayOutputStream bStream;
        ObjectOutput sendData;
        byte[] serializedMessage;
        DatagramPacket sendPacket;

		while(true){
			if(colaEnvioTemasDisponibles.peek()!=null){
                System.out.println("Entre");
				m = colaEnvioTemasDisponibles.peek().getKey();
				clienteGestor = gestor.findCliente(m.getNombreUsuario());
				if(clienteGestor==null){
					System.out.println("El cliente "+m.getNombreUsuario()+ " no se encuentra registrado.");							
				}
				else{
                    try {
                        clientSocket = new DatagramSocket();       
                        IPAddress = clienteGestor.getIp();    			
                        bStream = new ByteArrayOutputStream();
                        sendData = new ObjectOutputStream(bStream);
                        m.setTipo(Mensaje.Tipo.TEMAS);
                        m.setTemas(this.getTemas());
                        m.setNombreUsuario("El gestor");
                        sendData.writeObject(m);
                        sendData.close();
                        serializedMessage = bStream.toByteArray();
                        sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, clienteGestor.getPuerto());
                        clientSocket.send(sendPacket);
                        System.out.println("Se ha enviado al cliente "+ clienteGestor.getNombreUsuario()+ " la lista de temas.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e); 
                    }
				}
				System.out.println("Envio de temas terminado!");
				colaEnvioTemasDisponibles.remove();
			}
		} 
		/*
		try {
			while(true){
				if(colaSubscripcionesFuente.peek()!=null){
					System.out.println("Subscripción fuente terminada!");
					colaSubscripcionesFuente.remove();
				}
			} 
	  	} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		*/
    }
    
    private List<String> getTemas(){
        List<String> retorno = new ArrayList<String>();
        for(TemaGestor tema: gestor.getTemas()){
            retorno.add(tema.getNombre());
        }
        return retorno;
    }
}