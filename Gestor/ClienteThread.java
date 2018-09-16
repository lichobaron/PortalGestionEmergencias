package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;
import java.util.Vector;

class ClienteThread extends Thread {

	private Gestor gestor;
	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente;
	private int modo;

	public ClienteThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente, int modo){
		this.gestor = gestor;
		this.colaSubscripcionesCliente = colaSubscripcionesCliente;
		this.modo = modo;
	}

	public void setModo(int modo) {
		this.modo = modo;
	}

	public void run () {

		InetAddress ipCliente;
		int puertoCliente;
		Mensaje m;

		while(true){
			if(colaSubscripcionesCliente.peek()!=null){
				for(Pair<InetAddress,Integer> b: gestor.getBackups()){
					Mensaje mc = colaSubscripcionesCliente.peek().getKey();
					mc.setIp(colaSubscripcionesCliente.peek().getValue().getKey());
					mc.setPuerto(colaSubscripcionesCliente.peek().getValue().getValue());
					this.sendMessage(mc, b.getKey(), b.getValue());
				}
				m = colaSubscripcionesCliente.peek().getKey();
				ClienteGestor clienteGestor = gestor.findCliente(m.getNombreUsuario());
				if(clienteGestor==null){
					ipCliente = colaSubscripcionesCliente.peek().getValue().getKey();
					puertoCliente = colaSubscripcionesCliente.peek().getValue().getValue();
					ClienteGestor c = new ClienteGestor(ipCliente,puertoCliente,m.getNombreUsuario());
					gestor.addCliente(c);
					System.out.println("El cliente "+ c.getNombreUsuario()+" ha sido registrado.");
					for(String t : m.getTemas()){
						TemaGestor tg = gestor.findTema(t);
						if(tg!=null){
							tg.addCliente(c);
							System.out.println("El tema "+ t + "ha sido registrado al cliente "+ c.getNombreUsuario());
						}
						else{
							if(modo==1){
								Mensaje me = new Mensaje(Mensaje.Tipo.ERROR, "El tema "+t+" del cliente "+c.getNombreUsuario()+ " no existe.", "El gestor");
								sendMessage(me, ipCliente, puertoCliente);
							}
							System.out.println("El tema "+t+" del cliente "+c.getNombreUsuario()+ " no existe.");
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
							if(icg==null){
								InfoContextoGestor newIct = new InfoContextoGestor(data);
								newIct.addCliente(c);
								tcg.addInfoContexto(newIct);
							}
							else{
								icg.addCliente(c);
							}
							System.out.println("El tema de contexto "+ data +" con categoría "+ict+" ha sido registrado al cliente "+ c.getNombreUsuario());
						}
						else{
							if(modo==1){
								Mensaje me = new Mensaje(Mensaje.Tipo.ERROR,"Categoría de contexto "+ict+" inválida.", "El gestor");
								sendMessage(me, ipCliente, puertoCliente);
							}
							System.out.println("Categoría de contexto "+ict+" inválida.");
						}
					}
					System.out.println("Subscripción cliente terminada!"); 
				}
				else{
					clienteGestor.setIp(colaSubscripcionesCliente.peek().getValue().getKey());
					clienteGestor.setPuerto(colaSubscripcionesCliente.peek().getValue().getValue());
					System.out.println("El cliente "+ m.getNombreUsuario()+" ya existe."); 
				}
				colaSubscripcionesCliente.remove();
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