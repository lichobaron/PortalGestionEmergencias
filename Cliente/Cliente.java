package javeriana.edu.co;

import java.net.*;
import java.io.*;

import javeriana.edu.co.Mensaje;

class Cliente {
	public static void main(String[] args) {
		Mensaje prueba = new Mensaje(Mensaje.Tipo.SUBSCLIE, "Holi", "kokoloko4");
		prueba.addTema("Inundacionesss");
		prueba.addInfoContext("residencia:cachipay");
		prueba.addInfoContext("edad:18");
		System.out.println(prueba);
		Mensaje prueba2 = new Mensaje(Mensaje.Tipo.TEMAS, "Holi", "kokoloko4");
		byte[] receiveData = new byte[1024];
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
			System.out.println("Mensaje enviado");
          			
			bStream = new ByteArrayOutputStream();
			sendData = new ObjectOutputStream(bStream); 
			sendData.writeObject(prueba2);
			sendData.close();
			serializedMessage = bStream.toByteArray();
			sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, 9876);
			clientSocket.send(sendPacket);
			System.out.println("Mensaje2 enviado");

			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				InetAddress ipMensaje = receivePacket.getAddress();
				int puertoMensaje = receivePacket.getPort();
				byte[] data = receivePacket.getData();
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
				Mensaje mensaje = (Mensaje)iStream.readObject();
				iStream.close();
				System.out.println(mensaje.toString());
			}
          	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}