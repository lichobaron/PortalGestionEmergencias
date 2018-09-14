package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import javeriana.edu.co.Mensaje;
import javeriana.edu.co.Cliente;
import javeriana.edu.co.Tema;
import javeriana.edu.co.Fuente;
import java.util.Vector;


class Gestor {

	public static enum Tipo {
		SUBSCLIE, SUBSFUEN, NOTICI
	}

	public static ConcurrentLinkedQueue<Mensaje> colaSubscripcionesCliente;
	public static ConcurrentLinkedQueue<Mensaje> colaSubscripcionesFuente;
	public static ConcurrentLinkedQueue<Mensaje> colaEnviodeMensajes;

	private Vector<Cliente> clientes;
	private Vector<Tema> temas;
	private Vector<Fuente> fuentes;
	private Vector<Mensaje> mensajes;

	public static void main(String[] args) {
		colaSubscripcionesCliente = new ConcurrentLinkedQueue<>(); 
		colaSubscripcionesFuente = new ConcurrentLinkedQueue<>(); 
		colaEnviodeMensajes = new ConcurrentLinkedQueue<>(); 

		ClienteThread clienteThread = new ClienteThread(colaSubscripcionesCliente);
		FuenteThread fuenteThread = new FuenteThread(colaSubscripcionesFuente);
		PublishMessageThread publishMessageThread = new PublishMessageThread(colaEnviodeMensajes);

		clienteThread.start();
		fuenteThread.start();
		publishMessageThread.start();

		try {
			DatagramSocket serverSocket = new DatagramSocket(9876);
			byte[] receiveData = new byte[1024];
			while (true) {

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				byte[] data = receivePacket.getData();
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
				Mensaje mensaje = (Mensaje)iStream.readObject();
				iStream.close();
				
				if (mensaje != null){
					System.out.println(mensaje);
					switch(mensaje.getTipo()) {
						case SUBSCLIE:
							System.out.println("Esperando subscripción cliente");
							colaSubscripcionesCliente.add(mensaje);
						   	break;
						case SUBSFUEN:
							System.out.println("Esperando subscripción fuente");
							colaSubscripcionesFuente.add(mensaje);
						   	break;
						case NOTICI:
							System.out.println("Esperando envío de noticias");
							colaEnviodeMensajes.add(mensaje);
						   	break;
						default :
							System.out.println("Mensaje inválido"); 
						   	break;
					 }				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}

class ClienteThread extends Thread {

	private ConcurrentLinkedQueue<Mensaje> colaSubscripcionesCliente;

	public ClienteThread(ConcurrentLinkedQueue<Mensaje> colaSubscripcionesCliente){
		this.colaSubscripcionesCliente = colaSubscripcionesCliente;
	}

	public void run () {
		/*
		while(true){
			if(colaSubscripcionesCliente.peek()!=null){
				System.out.println("Subscripción cliente terminada!"); 
				colaSubscripcionesCliente.remove();
			}
		}*/  
	  	try {
			while(true){
				if(colaSubscripcionesCliente.peek()!=null){
					sleep(10000);
					System.out.println("Subscripción cliente terminada!"); 
					colaSubscripcionesCliente.remove();
				}
			} 
	  	} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}

class FuenteThread extends Thread {

	private ConcurrentLinkedQueue<Mensaje> colaSubscripcionesFuente;

	public FuenteThread(ConcurrentLinkedQueue<Mensaje> colaSubscripcionesFuente){
		this.colaSubscripcionesFuente = colaSubscripcionesFuente;
	}

	public void run () {
		while(true){
			if(colaSubscripcionesFuente.peek()!=null){
				System.out.println("Subscripción fuente terminada!");
				colaSubscripcionesFuente.remove();
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
}

class PublishMessageThread extends Thread {

	private ConcurrentLinkedQueue<Mensaje> colaEnviodeMensajes;

	public PublishMessageThread(ConcurrentLinkedQueue<Mensaje> colaEnviodeMensajes){
		this.colaEnviodeMensajes = colaEnviodeMensajes;
	}

	public void run () {
		while(true){
			if(colaEnviodeMensajes.peek()!=null){
				System.out.println("Envío de mensajes terminado!"); 
				colaEnviodeMensajes.remove();
			}
		} 
		/*
		try {
			while(true){
				if(colaEnviodeMensajes.peek()!=null){
					System.out.println("Envío de mensajes terminado!"); 
					colaEnviodeMensajes.remove();
				}
			} 
	  	} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		*/
	}
}