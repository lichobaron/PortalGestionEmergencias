package javeriana.edu.co;

import java.net.Socket;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

import javeriana.edu.co.Mensaje;

class Fuente {
	public static void main(String[] args) {
		Mensaje prueba = new Mensaje(Mensaje.Tipo.SUBSFUEN, "Holi");
		prueba.addTema("Inundaciones");
		System.out.println(prueba);
		try {
			Socket clientSocket = new Socket("localhost", 6789);
			OutputStream outToServer = clientSocket.getOutputStream();
			ObjectOutputStream objectOTS = new ObjectOutputStream(outToServer);

			objectOTS.writeObject(prueba);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e); 
		}
	}
}