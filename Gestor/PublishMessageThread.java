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

		while(true){
			if(colaEnviodeMensajes.peek()!=null){
                m = colaEnviodeMensajes.peek().getKey();
                InetAddress ipFuente= colaEnviodeMensajes.peek().getValue().getKey();
				int puertoFuente = colaEnviodeMensajes.peek().getValue().getValue();
                if(gestor.existFuente(m.getNombreUsuario())){
                    m.setNombreUsuario("");
                    for(String temaFuente: m.getTemas()){
                        TemaGestor temaGestor = gestor.findTema(temaFuente);
                        if(temaGestor!=null){
                            for(ClienteGestor cliente: temaGestor.getClientes()){
                                try {
                                    if(!this.existCliente(cliente.getNombreUsuario())){
                                        sendMessage(m, cliente.getIp(),cliente.getPuerto() );
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
                                            sendMessage(m, cliente.getIp(),cliente.getPuerto() );
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
                            Mensaje me = new Mensaje(Mensaje.Tipo.ERROR, "La categoría de contexto "+ict+" no es valida", "El gestor");
							sendMessage(me, ipFuente, puertoFuente);
                            System.out.println("La categoría de contexto "+ict+ " no es válida");
                        }                                             
                    }
                }
                else{
                    Mensaje me = new Mensaje(Mensaje.Tipo.ERROR, "La fuente "+ m.getNombreUsuario()+" no se encuentra registrada.", "El gestor");
                    sendMessage(me, ipFuente, puertoFuente);
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