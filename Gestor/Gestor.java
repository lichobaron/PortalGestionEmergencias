package javeriana.edu.co;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javeriana.edu.co.Mensaje;

class Gestor {
	public static void main(String[] args) {
		try {
			ServerSocket clienteConexion = new ServerSocket(6789);
			while (true) {
				Socket conexion = clienteConexion.accept();
				InputStream inFromClient = conexion.getInputStream();
				ObjectInputStream objectIFC = new ObjectInputStream(inFromClient);
				Mensaje prueba = (Mensaje)objectIFC.readObject();
				if (prueba != null) 
					System.out.println(prueba);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}