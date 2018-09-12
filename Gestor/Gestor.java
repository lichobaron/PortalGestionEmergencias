package javeriana.edu.co;

import java.net.*;
import java.io.*;

import javeriana.edu.co.Mensaje;

class Gestor {
	public static void main(String[] args) {
		try {
			DatagramSocket serverSocket = new DatagramSocket(9876);
			byte[] receiveData = new byte[1024];
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				byte[] data = receivePacket.getData();
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
				Mensaje prueba = (Mensaje)iStream.readObject();
				iStream.close();
				if (prueba != null) 
					System.out.println(prueba);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}