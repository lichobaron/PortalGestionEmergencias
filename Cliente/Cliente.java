package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

import javeriana.edu.co.Mensaje;

class Cliente {
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Uso: [Ejecutable] [nombreUsuario] [ipServidor] "
							   + "[puertoServidor]");
			return;
		}
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress ipServidor = InetAddress.getByName(args[1]);
			int puertoServidor = Integer.valueOf(args[2]);

			byte[] serializedMessage;
			byte[] receiveData = new byte[1024];
			DatagramPacket sendPacket;
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);

			ByteArrayOutputStream bOStream;
			ObjectOutput sendData;
			ByteArrayInputStream bIStream;
			ObjectInputStream iStream;

			String residenciaUsuario, generoUsuario, etniaUsuario, temasUsuario;
			String edadUsuario, aux;
			Mensaje suscripción = new Mensaje (Mensaje.Tipo.SUBSCLIE);
			suscripción.setNombreUsuario(args[0]);

			System.out.println("Cual es tu edad: ");
			edadUsuario = System.console().readLine();
			System.out.println(edadUsuario);
			if (!edadUsuario.equals(""))
				suscripción.addInfoContext("edad:" + edadUsuario);

			System.out.println("En donde resides: ");
			residenciaUsuario = System.console().readLine();
			if (!residenciaUsuario.equals(""))
				suscripción.addInfoContext("residencia:" + residenciaUsuario);

			System.out.println("Cual es tu genero: ");
			generoUsuario = System.console().readLine();
			if (!generoUsuario.equals(""))
				suscripción.addInfoContext("genero:" + generoUsuario);

			System.out.println("Cual es tu grupo etnico: ");
			etniaUsuario = System.console().readLine();
			if (!etniaUsuario.equals(""))
				suscripción.addInfoContext("grupo etnico:" + etniaUsuario);
          	
          	Mensaje adquirirTemas = new Mensaje(Mensaje.Tipo.TEMAS);
			bOStream = new ByteArrayOutputStream();
			sendData = new ObjectOutputStream(bOStream);
			sendData.writeObject(adquirirTemas);
			serializedMessage = bOStream.toByteArray();
			sendPacket = new DatagramPacket(serializedMessage,
				                            serializedMessage.length,
				                            ipServidor, puertoServidor);
			clientSocket.send(sendPacket);

			System.out.println("Adquiriendo Temas...");
			clientSocket.receive(receivePacket);
			byte[] data = receivePacket.getData();
			bIStream = new ByteArrayInputStream(data);
			iStream = new ObjectInputStream(bIStream);
			Mensaje temas = (Mensaje)iStream.readObject();
			System.out.println("Los Temas Disponibles son:");
			System.out.println(temas.getTemas());

			System.out.println("Ingrese lo temas de interes separados por (;).");
			temasUsuario = System.console().readLine();

			StringTokenizer tokens = new StringTokenizer(temasUsuario, ";");
			while (tokens.hasMoreTokens()) {
				aux = tokens.nextToken();
				suscripción.addTema(aux.toLowerCase());
			}

			bOStream = new ByteArrayOutputStream();
			sendData = new ObjectOutputStream(bOStream);
			sendData.writeObject(suscripción);
			serializedMessage = bOStream.toByteArray();
			sendPacket = new DatagramPacket(serializedMessage,
				                            serializedMessage.length,
				                            ipServidor, puertoServidor);
			clientSocket.send(sendPacket);

			while (true) {
				clientSocket.receive(receivePacket);

				data = receivePacket.getData();
				bIStream = new ByteArrayInputStream (data);
				iStream = new ObjectInputStream (bIStream);
				Mensaje receiveMensaje = (Mensaje)iStream.readObject();

				iStream.close();
				bIStream.close();

				switch (receiveMensaje.getTipo()) {
					case NOTICI: {
						System.out.println(receiveMensaje.getCuerpo());
					}
					case UPT: {
						ipServidor = receivePacket.getAddress();
						puertoServidor = receivePacket.getPort();
					}
					case ERROR: {
						System.out.println(receiveMensaje.getCuerpo());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}