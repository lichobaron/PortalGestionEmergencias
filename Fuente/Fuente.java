package javeriana.edu.co;

import java.net.*;
import java.io.*;

import javeriana.edu.co.Mensaje;

class Fuente {
	public static void main(String[] args) {
		Mensaje prueba = new Mensaje(Mensaje.Tipo.SUBSFUEN, "Holi","SoyFuente");
		Mensaje prueba2 = new Mensaje(Mensaje.Tipo.NOTICI, "Holi","SoyFuente");
		prueba2.addTema("Incendios");
		prueba2.addInfoContext("residencia:cachipay");
		prueba2.addInfoContext("edad:18");
		System.out.println(prueba);
		System.out.println(prueba2);
		try {
			DatagramSocket clientSocket = new DatagramSocket();       
			InetAddress IPAddress = InetAddress.getByName("localhost");       			
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput sendData = new ObjectOutputStream(bStream); 
			sendData.writeObject(prueba);
			sendData.close();
			byte[] serializedMessage = bStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, 9876);
			clientSocket.send(sendPacket); 
			
			bStream = new ByteArrayOutputStream();
			sendData = new ObjectOutputStream(bStream); 
			sendData.writeObject(prueba2);
			sendData.close();
			serializedMessage = bStream.toByteArray();
			sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, 9876);
			clientSocket.send(sendPacket);   
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e); 
		}
	}
}