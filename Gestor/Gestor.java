package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.*;

import javeriana.edu.co.Mensaje;
import javeriana.edu.co.ClienteGestor;
import javeriana.edu.co.TemaGestor;
import javeriana.edu.co.FuenteGestor;
import java.util.Vector;


class Gestor {

	public static enum Tipo {
		SUBSCLIE, SUBSFUEN, NOTICI
	}

	private static ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente;
	private static ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente;
	private static ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes;

	public static Vector<ClienteGestor> clientes;
	public static Vector<TemaGestor> temas;
	public static Vector<FuenteGestor> fuentes;
	

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
						default :
							System.out.println("Mensaje inválido"); 
						   	break;
					}				
				}
				//Prueba Temas
				/*for(TemaGestor c : temas){
					System.out.println(c.toString());
				}
				//Prueba Fuente
				for(FuenteGestor c : fuentes){
					System.out.println(c.toString());
				}
				//Prueba Cliente
				for(ClienteGestor c : clientes){
					System.out.println(c.toString());
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

}

class ClienteThread extends Thread {

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente;
	private InetAddress ipCliente;
	private int puertoCliente;

	public ClienteThread(ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente){
		this.colaSubscripcionesCliente = colaSubscripcionesCliente;
	}

	public void run () {
		
		while(true){
			if(colaSubscripcionesCliente.peek()!=null){
				String errors = "";
				boolean added = false;
				Mensaje m = colaSubscripcionesCliente.peek().getKey();
				this.ipCliente = colaSubscripcionesCliente.peek().getValue().getKey();
				this.puertoCliente = colaSubscripcionesCliente.peek().getValue().getValue();
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

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente;
	private InetAddress ipFuente;
	private int puertoFuente;

	public FuenteThread(ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente){
		this.colaSubscripcionesFuente = colaSubscripcionesFuente;
	}

	public void run () {
		while(true){
			if(colaSubscripcionesFuente.peek()!=null){
				Mensaje m = colaSubscripcionesFuente.peek().getKey();
				this.ipFuente = colaSubscripcionesFuente.peek().getValue().getKey();
				this.puertoFuente = colaSubscripcionesFuente.peek().getValue().getValue();
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

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes;

	public PublishMessageThread(ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes){
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