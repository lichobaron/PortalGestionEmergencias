package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;
import java.util.Vector;


class MainGestor {

	public static void main(String[] args) {
		try {
			DatagramSocket serverSocket = new DatagramSocket(9876);
			byte[] receiveData = new byte[1024];

			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente = new ConcurrentLinkedQueue<>(); 
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente = new ConcurrentLinkedQueue<>(); 
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes = new ConcurrentLinkedQueue<>();
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnvioTemasDisponibles = new ConcurrentLinkedQueue<>();
			
			Gestor gestor = new Gestor();

			ClienteThread clienteThread = new ClienteThread(gestor, colaSubscripcionesCliente);
			FuenteThread fuenteThread = new FuenteThread(gestor, colaSubscripcionesFuente);
			PublishMessageThread publishMessageThread = new PublishMessageThread(gestor, colaEnviodeMensajes, serverSocket);
			TopicsThread topicsThread = new TopicsThread(gestor, colaEnvioTemasDisponibles, serverSocket);

			clienteThread.start();
			fuenteThread.start();
			publishMessageThread.start();
			topicsThread.start();
	
			while (true) {

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				InetAddress ipMensaje = receivePacket.getAddress();
				int puertoMensaje = receivePacket.getPort();
				byte[] data = receivePacket.getData();
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
				Mensaje mensaje = (Mensaje)iStream.readObject();
				Pair<InetAddress,Integer> t1= new Pair<>(ipMensaje,puertoMensaje);
				Pair<Mensaje,Pair<InetAddress,Integer>> t2 = new Pair<>(mensaje,t1);
				iStream.close();
				
				if (mensaje != null){
					System.out.println(mensaje);
					switch(mensaje.getTipo()) {
						case SUBSCLIE:
							System.out.println("Esperando subscripción cliente");
							colaSubscripcionesCliente.add(t2);
						   	break;
						case SUBSFUEN:
							System.out.println("Esperando subscripción fuente");
							colaSubscripcionesFuente.add(t2);
						   	break;
						case NOTICI:
							System.out.println("Esperando envío de noticias");
							colaEnviodeMensajes.add(t2);
							   break;
						case TEMAS:
							System.out.println("Esperando envío de temas");
							colaEnvioTemasDisponibles.add(t2);
						  break;
						default :
							System.out.println("Mensaje inválido"); 
						   	break;
					}				
				}
				/*
				//Prueba Temas
				for(TemaGestor c : gestor.getTemas()){
					System.out.println(c.toString());
				}
				//Prueba Fuente
				for(FuenteGestor c : gestor.getFuentes()){
					System.out.println(c.toString());
				}
				System.out.println("Clientes");
				//Prueba Cliente
				for(ClienteGestor c : gestor.getClientes()){
					System.out.println(c.toString());
				}
				*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}