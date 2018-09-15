package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair; 

import javeriana.edu.co.*;
import java.util.Vector;

class FuenteThread extends Thread {

	private ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente;
	private Gestor gestor;

	public FuenteThread(Gestor gestor, ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente){
		this.colaSubscripcionesFuente = colaSubscripcionesFuente;
		this.gestor = gestor;
	}

	public void run () {

		InetAddress ipFuente;
		int puertoFuente;
		Mensaje m;
		while(true){
			if(colaSubscripcionesFuente.peek()!=null){
				m = colaSubscripcionesFuente.peek().getKey();
				if(!gestor.existFuente(m.getNombreUsuario())){
					ipFuente = colaSubscripcionesFuente.peek().getValue().getKey();
					puertoFuente = colaSubscripcionesFuente.peek().getValue().getValue();
					FuenteGestor f = new FuenteGestor(ipFuente,puertoFuente,m.getNombreUsuario());
					gestor.addFuente(f);
					System.out.println("La fuente "+m.getNombreUsuario()+ " fue agregada.");							
				}
				else{
					System.out.println("La fuente "+m.getNombreUsuario()+ " ya existe.");
				}
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