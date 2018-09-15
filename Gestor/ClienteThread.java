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

	public ClienteThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente){
		this.gestor = gestor;
		this.colaSubscripcionesCliente = colaSubscripcionesCliente;
	}

	public void run () {

		InetAddress ipCliente;
		int puertoCliente;
		Mensaje m;

		while(true){
			if(colaSubscripcionesCliente.peek()!=null){

				m = colaSubscripcionesCliente.peek().getKey();
				if(!gestor.existCliente(m.getNombreUsuario())){
					ipCliente = colaSubscripcionesCliente.peek().getValue().getKey();
					puertoCliente = colaSubscripcionesCliente.peek().getValue().getValue();
					ClienteGestor c = new ClienteGestor(ipCliente,puertoCliente,m.getNombreUsuario());
					gestor.addCliente(c);
					System.out.println("El cliente "+ c.getNombreUsuario()+" ha sido registrado.");
					for(String t : m.getTemas()){
						TemaGestor tg = gestor.findTema(t);
						if(tg!=null){
							tg.addCliente(c);
						}
						else{
							TemaGestor tNuevo = new TemaGestor(t);
							tNuevo.addCliente(c);
							gestor.addTema(tNuevo);
						}
						System.out.println("El tema "+ t + "ha sido registrado al cliente "+ c.getNombreUsuario());
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
							System.out.println("Categoría de contexto inválida.");
						}
					}

					System.out.println("Subscripción cliente terminada!"); 
				}
				else{
					System.out.println("El cliente "+ m.getNombreUsuario()+" ya existe."); 
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