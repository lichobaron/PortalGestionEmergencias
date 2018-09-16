package javeriana.edu.co;

import java.net.*;
import java.io.*;

import javeriana.edu.co.Mensaje;

class Cliente {
	public static void main(String[] args) {
		Mensaje prueba = new Mensaje(Mensaje.Tipo.SUBSCLIE, "Holi", "kokoloko4");
		prueba.addTema("Inundaciones");
		prueba.addInfoContext("residencia:cachipay");
		prueba.addInfoContext("edad:18");
		System.out.println(prueba);
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
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e); 
		}
	}
}