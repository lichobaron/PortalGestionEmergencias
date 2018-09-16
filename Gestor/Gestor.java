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
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();  
				Mensaje respuesta = new Mensaje (Mensaje.Tipo.ERROR, "Esto es un error");
				ByteArrayOutputStream bStream = new ByteArrayOutputStream ();
				ObjectOutputStream sendData = new ObjectOutputStream (bStream); 
				sendData.writeObject(respuesta);
				byte[] serializedMessage = bStream.toByteArray();
				DatagramPacket sendPacket=new DatagramPacket(serializedMessage, 
					serializedMessage.length, IPAddress, port);
				serverSocket.send(sendPacket);
				System.out.println("envio sendPacket" + "\n" + IPAddress + " " + Integer.toString(port));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}