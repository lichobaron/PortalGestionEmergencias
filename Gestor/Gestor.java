package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import javeriana.edu.co.Mensaje;
import javeriana.edu.co.ClienteGestor;
import javeriana.edu.co.TemaGestor;
import javeriana.edu.co.FuenteGestor;
import java.util.Vector;


class Gestor {

	public static enum Tipo {
		SUBSCLIE, SUBSFUEN, NOTICI
	}

	public static ConcurrentLinkedQueue<Mensaje> colaSubscripcionesCliente;
	public static ConcurrentLinkedQueue<Mensaje> colaSubscripcionesFuente;
	public static ConcurrentLinkedQueue<Mensaje> colaEnviodeMensajes;

	public static Vector<ClienteGestor> clientes;
	public static Vector<TemaGestor> temas;
	public static Vector<FuenteGestor> fuentes;

	public static InetAddress ipMensaje;
	public static int puertoMensaje;

	public static void main(String[] args) {


		colaSubscripcionesCliente = new ConcurrentLinkedQueue<>(); 
		colaSubscripcionesFuente = new ConcurrentLinkedQueue<>(); 
		colaEnviodeMensajes = new ConcurrentLinkedQueue<>(); 

		clientes = new Vector<ClienteGestor>();
		temas = new Vector<TemaGestor>();
		fuentes = new Vector<FuenteGestor>();

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
				ipMensaje = receivePacket.getAddress();
				puertoMensaje = receivePacket.getPort();
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
	private InetAddress ipCliente;
	private int puertoCliente;

	public ClienteThread(ConcurrentLinkedQueue<Mensaje> colaSubscripcionesCliente){
		this.colaSubscripcionesCliente = colaSubscripcionesCliente;
	}

	public void run () {
		
		while(true){
			if(colaSubscripcionesCliente.peek()!=null){
				String errors = "";
				boolean added = false;
				Mensaje m = colaSubscripcionesCliente.peek();
				this.ipCliente = Gestor.ipMensaje;
				this.puertoCliente = Gestor.puertoMensaje;
				ClienteGestor c = new ClienteGestor(this.ipCliente,this.puertoCliente,m.getNombreUsuario());
				Gestor.clientes.add(c);
				for(String s : m.getTemas()){
					added = false;
					for(TemaGestor t : Gestor.temas){
						if(t.getNombre().equals(s)){
							boolean exist = false;
							for(ClienteGestor u: t.getClientes()){
								if(u.getNombreUsuario().equals(u.getNombreUsuario())){
									exist = true;
									break;
								}		
							}
							if(!exist){
								System.out.println("Entré");
								t.addCliente(c);
								added = true;
								break;
							}else{
								errors += "El cliente ya se encuentra registrado con el tema "+ s+"\n";
							}
						}
					}
					if(!added){
						TemaGestor tNuevo = new TemaGestor(s);
						tNuevo.addCliente(c);
						Gestor.temas.add(tNuevo);
						break;
					}	
				}
				if(errors == ""){
					System.out.println("Subscripción cliente terminada!"); 
				}else{
					System.out.println(errors); 
				}
				colaSubscripcionesCliente.remove();
			}
		}
	  	/*try {
			while(true){
				if(colaSubscripcionesCliente.peek()!=null){
					sleep(1000);
					System.out.println("Subscripción cliente terminada!"); 
					colaSubscripcionesCliente.remove();
				}
			} 
	  	} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e);
		}*/
	}
}

class FuenteThread extends Thread {

	private ConcurrentLinkedQueue<Mensaje> colaSubscripcionesFuente;
	private InetAddress ipFuente;
	private int puertoFuente;

	public FuenteThread(ConcurrentLinkedQueue<Mensaje> colaSubscripcionesFuente){
		this.colaSubscripcionesFuente = colaSubscripcionesFuente;
	}

	public void run () {
		while(true){
			if(colaSubscripcionesFuente.peek()!=null){
				Mensaje m = colaSubscripcionesFuente.peek();
				this.ipFuente = Gestor.ipMensaje;
				this.puertoFuente = Gestor.puertoMensaje;
				FuenteGestor f = new FuenteGestor(this.ipFuente,this.puertoFuente,m.getNombreUsuario());
				Gestor.fuentes.add(f);	
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