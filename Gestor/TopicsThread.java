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
        int puerto;
        ByteArrayOutputStream bStream;
        ObjectOutput sendData;
        byte[] serializedMessage;
        DatagramPacket sendPacket;

		while(true){
			if(colaEnvioTemasDisponibles.peek()!=null){
				m = colaEnvioTemasDisponibles.peek().getKey();
				clienteGestor = gestor.findCliente(m.getNombreUsuario());
				if(clienteGestor==null){
                    IPAddress = colaEnvioTemasDisponibles.peek().getValue().getKey();
					puerto = colaEnvioTemasDisponibles.peek().getValue().getValue();					
				}
				else{
                    IPAddress = clienteGestor.getIp();
                    puerto = clienteGestor.getPuerto();
                }
                m.setTipo(Mensaje.Tipo.TEMAS);
                m.setTemas(this.getTemas());
                m.setNombreUsuario("El gestor");
                sendMessage(m, IPAddress, puerto);
                if(clienteGestor==null){
                    System.out.println("**Se ha enviado al cliente sin registrar la lista de temas.**");					
				}
				else{
                    System.out.println("**Se ha enviado al cliente "+ clienteGestor.getNombreUsuario()+ " la lista de temas.**");
                }
                this.gestor.saveState(this.gestor.getIpGestor(), this.gestor.getPuertoGestor());
				System.out.println("**Envio de temas terminado!**\n");
				colaEnvioTemasDisponibles.remove();
			}
        }
    }
    
    private List<String> getTemas(){
        List<String> retorno = new ArrayList<String>();
        for(TemaGestor tema: gestor.getTemas()){
            retorno.add(tema.getNombre());
        }
        return retorno;
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