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
    private int modo;

	public PublishMessageThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes, DatagramSocket serverSocket, int modo){
		this.colaEnviodeMensajes = colaEnviodeMensajes;
        this.gestor = gestor;
        this.serverSocket = serverSocket;
        this.clientesEnviados = new Vector<ClienteGestor>();
        this.modo = modo;
    }
    
    public void setModo(int modo) {
        this.modo = modo;
    }

	public void run () {

        Mensaje m;

		while(true){
			if(colaEnviodeMensajes.peek()!=null){
                for(Pair<InetAddress,Integer> b: gestor.getBackups()){
					Mensaje mc = colaEnviodeMensajes.peek().getKey();
					mc.setIp(colaEnviodeMensajes.peek().getValue().getKey());
					mc.setPuerto(colaEnviodeMensajes.peek().getValue().getValue());
					this.sendMessage(mc, b.getKey(), b.getValue());
				}
                m = colaEnviodeMensajes.peek().getKey();
                InetAddress ipFuente= colaEnviodeMensajes.peek().getValue().getKey();
				int puertoFuente = colaEnviodeMensajes.peek().getValue().getValue();
                if(gestor.existFuente(m.getNombreUsuario())){
                    gestor.findFuente(m.getNombreUsuario()).addNoticia(m);
                    System.out.println("**Noticia de "+m.getNombreUsuario()+" guardada.**");
                    m.setNombreUsuario("");
                    for(String temaFuente: m.getTemas()){
                        TemaGestor temaGestor = gestor.findTema(temaFuente);
                        if(temaGestor!=null){
                            if(modo==1){
                                for(ClienteGestor cliente: temaGestor.getClientes()){
                                    try {
                                        if(!this.existCliente(cliente.getNombreUsuario())){
                                            sendMessage(m, cliente.getIp(),cliente.getPuerto());
                                            clientesEnviados.add(cliente);
                                            System.out.println("\tSe ha enviado al cliente "+ cliente.getNombreUsuario()+ " una noticia de "+ temaFuente);
                                        }       	
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.out.println(e); 
                                    }
                                }
                            }
                        }
                        else{
                            TemaGestor newTema = new TemaGestor(temaFuente);
                            gestor.addTema(newTema);
                            System.out.println("//Se ha creado el tema "+temaFuente+" y no se ha realizado el envío de mensajes//");
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
                                if (modo == 1){
                                    for(ClienteGestor cliente: icg.getClientes()){
                                        try {
                                            if(!this.existCliente(cliente.getNombreUsuario())){
                                                sendMessage(m, cliente.getIp(),cliente.getPuerto());
                                                clientesEnviados.add(cliente);
                                                System.out.println("\t**Se ha enviado al cliente "+ cliente.getNombreUsuario()+ " una noticia de "+ data+"**\n");
                                            }      	
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println(e); 
                                        }
                                    }
                                }
                            }
                            else{
                                tcg.addInfoContexto(new InfoContextoGestor(data));
                                System.out.println("//Se ha creado el tema de contexto "+data+" se ha agregado a la categoría "+ict+" y no se ha realizado el envío de mensajes//");
                            }
                        }
                        else{
                            if(modo == 1){
                                Mensaje me = new Mensaje(Mensaje.Tipo.ERROR, "La categoría de contexto "+ict+" no es valida", "El gestor");
                                sendMessage(me, ipFuente, puertoFuente);
                            }
                            System.out.println("//La categoría de contexto "+ict+ " no es válida//");
                        }                                             
                    }
                }
                else{
                    if(modo==1){
                        Mensaje me = new Mensaje(Mensaje.Tipo.ERROR, "La fuente "+ m.getNombreUsuario()+" no se encuentra registrada.", "El gestor");
                        sendMessage(me, ipFuente, puertoFuente);
                    }
                    System.out.println("//La fuente "+ m.getNombreUsuario()+" no se encuentra registrada.//");
                }
                System.out.println("**Envío de mensajes terminado!**\n");
                this.gestor.saveState(this.gestor.getIpGestor(), this.gestor.getPuertoGestor());
                colaEnviodeMensajes.remove();
                clientesEnviados.clear();
			}
		} 
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
            byte[] serializedMessage = bStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, port);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}