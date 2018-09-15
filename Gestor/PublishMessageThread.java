package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;
import java.util.Vector;

class PublishMessageThread extends Thread {

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes;
    private Gestor gestor;
    private DatagramSocket serverSocket;
    private Vector<ClienteGestor> clientesEnviados;

	public PublishMessageThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes, DatagramSocket serverSocket){
		this.colaEnviodeMensajes = colaEnviodeMensajes;
        this.gestor = gestor;
        this.serverSocket = serverSocket;
        this.clientesEnviados = new Vector<ClienteGestor>();
	}

	public void run () {

        Mensaje m;
        DatagramSocket clientSocket;
        InetAddress IPAddress;
        ByteArrayOutputStream bStream;
        ObjectOutput sendData;
        byte[] serializedMessage;
        DatagramPacket sendPacket;

		while(true){
			if(colaEnviodeMensajes.peek()!=null){
                m = colaEnviodeMensajes.peek().getKey();
                if(gestor.existFuente(m.getNombreUsuario())){
                    m.setNombreUsuario("");
                    for(String temaFuente: m.getTemas()){
                        TemaGestor temaGestor = gestor.findTema(temaFuente);
                        if(temaGestor!=null){
                            for(ClienteGestor cliente: temaGestor.getClientes()){
                                try {
                                    if(!this.existCliente(cliente.getNombreUsuario())){
                                        clientSocket = new DatagramSocket();       
                                        IPAddress = cliente.getIp();    			
                                        bStream = new ByteArrayOutputStream();
                                        sendData = new ObjectOutputStream(bStream); 
                                        sendData.writeObject(m);
                                        sendData.close();
                                        serializedMessage = bStream.toByteArray();
                                        sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, cliente.getPuerto());
                                        clientSocket.send(sendPacket);
                                        clientesEnviados.add(cliente);
                                        System.out.println("Se ha enviado al cliente "+ cliente.getNombreUsuario()+ "una noticia de "+ temaFuente);
                                    }       	
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println(e); 
                                }
                            }
                        }
                        else{
                            TemaGestor newTema = new TemaGestor(temaFuente);
                            gestor.addTema(newTema);
                            System.out.println("Se ha creado el tema "+temaFuente+" y no se ha realizado el envío de mensajes");
                        }
                    }
                    for(String ic: m.getInfoContext()){
                        String ict = "";
                        String data = "";
                        boolean second = false;
                        for(int i = 0; i < ic.length(); i++){
                            if( Character.toString(ic.charAt(i)).equals(":")){
                                second = true;
                            }
                            else if (!second){
                                ict+= Character.toString(ic.charAt(i));
                            }
                            else{
                                data+= Character.toString(ic.charAt(i));
                            }
                        }
                        TemaContextoGestor tcg = gestor.findInfoContexto(ict);
                        if(tcg!=null){
                            InfoContextoGestor icg = tcg.findInfoContexto(data);
                            if(icg!=null){
                                for(ClienteGestor cliente: icg.getClientes()){
                                    try {
                                        if(!this.existCliente(cliente.getNombreUsuario())){
                                            clientSocket = new DatagramSocket();       
                                            IPAddress = cliente.getIp();    			
                                            bStream = new ByteArrayOutputStream();
                                            sendData = new ObjectOutputStream(bStream); 
                                            sendData.writeObject(m);
                                            sendData.close();
                                            serializedMessage = bStream.toByteArray();
                                            sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, cliente.getPuerto());
                                            clientSocket.send(sendPacket);
                                            clientesEnviados.add(cliente);
                                            System.out.println("Se ha enviado al cliente "+ cliente.getNombreUsuario()+ " una noticia de "+ data);
                                        }      	
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.out.println(e); 
                                    }
                                }
                            }
                            else{
                                tcg.addInfoContexto(new InfoContextoGestor(data));
                                System.out.println("Se ha creado el tema de contexto "+data+" se ha agregado a la categoría "+ict+" y no se ha realizado el envío de mensajes");
                            }
                        }
                        else{
                            System.out.println("La categoría de contexto "+tcg.getNombre()+ " no es válida");
                        }                                             
                    }
                }
                else{
                    System.out.println("La fuente "+ m.getNombreUsuario()+" no se encuentra registrada.");
                }
				System.out.println("Envío de mensajes terminado!"); 
                colaEnviodeMensajes.remove();
                clientesEnviados.clear();
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
    
    private boolean existCliente(String username){
		int i = 0;
		while(i<this.clientesEnviados.size()){
			if(this.clientesEnviados.get(i).getNombreUsuario().equals(username)){
				return true;
			}
			i++;
		}
		return false;
	}
}