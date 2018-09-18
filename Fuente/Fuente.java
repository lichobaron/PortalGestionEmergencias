package javeriana.edu.co;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javeriana.edu.co.EnviarMensaje;
import javeriana.edu.co.Mensaje;

class Fuente {
	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Uso: [Ejecutable] [nombre usuario] [archivoNoticia] [ipGestor] [puertoGestor]");
			return;
		}
		try {
			DatagramSocket fuenteSocket = new DatagramSocket();
			InetAddress ipServidor = InetAddress.getByName(args[2]);

			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream sendData = new ObjectOutputStream(bStream);

			Mensaje suscripcion = new Mensaje(Mensaje.Tipo.SUBSFUEN, "");
			suscripcion.setNombreUsuario(args[0]);
			sendData.writeObject(suscripcion);

			byte[] serializedMessage = bStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(serializedMessage,
				serializedMessage.length, ipServidor, Integer.parseInt(args[3]));
			fuenteSocket.send(sendPacket);

			sendData.close();
			bStream.close();

			List<EnviarMensaje> mensajes = leerArchivo(args[0], args[1],
				fuenteSocket, ipServidor, Integer.parseInt(args[3]));
			if (0 < mensajes.size()) {
				for (EnviarMensaje mensaje : mensajes)
					mensaje.start();
			}
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				fuenteSocket.receive(receivePacket);

				byte[] data = receivePacket.getData();
				ByteArrayInputStream baIStream = new ByteArrayInputStream (data);
				ObjectInputStream iStream = new ObjectInputStream (baIStream);
				Mensaje receiveMensaje = (Mensaje)iStream.readObject();

				iStream.close();
				baIStream.close();

				switch (receiveMensaje.getTipo()) {
					case UPT: {
						if (0 < mensajes.size()) {
							for (EnviarMensaje mensaje : mensajes) {
								mensaje.setIpServidor(receivePacket.getAddress());
								mensaje.setPuerto(receivePacket.getPort());
							}
						}
						break;
					}
					case ERROR: {
						System.out.println(receiveMensaje.getCuerpo());
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e); 
		}
	}

	public static List<EnviarMensaje> leerArchivo(String user, String fileName,
		DatagramSocket fuenteSocket, InetAddress ipServidor, int puerto) {
		int milis, acum = 0;
		Scanner input;
		File file = new File(fileName);
		String hora = "", lineaTemas, lineaInfoContexto, titulo, cuerpo, auxS;
		StringTokenizer tokens;
		EnviarMensaje auxEM;
		List<String> temas = new ArrayList<String> ();
		List<String> infoContexto = new ArrayList<String> ();
		List<EnviarMensaje> mensajes = new ArrayList<EnviarMensaje> ();

		try {
			input = new Scanner(file);

			while (!hora.equals("0")) {
				hora = input.nextLine();
				if (!hora.equals("0")) {
					milis = convertirMilisHoraEnvio(hora);

					lineaTemas = input.nextLine();
					tokens = new StringTokenizer(lineaTemas, ";");
					while (tokens.hasMoreTokens()) {
						auxS = tokens.nextToken();
						temas.add(auxS);
					}

					lineaInfoContexto = input.nextLine();
					tokens = new StringTokenizer(lineaInfoContexto, ";");
					while (tokens.hasMoreTokens()) {
						auxS = tokens.nextToken();
						infoContexto.add(auxS);
					}

					titulo = input.nextLine();
					cuerpo = input.nextLine();
					auxEM = new EnviarMensaje (Integer.toString(acum), user,
						titulo + "\n" + cuerpo, temas, infoContexto, milis,
						fuenteSocket, ipServidor, puerto);
					mensajes.add(auxEM);
					temas = new ArrayList<String> ();
					infoContexto = new ArrayList<String> ();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e); 
		}

		return mensajes;
	}

	public static int convertirMilisHoraEnvio(String horaEnvio) {
		int hora, minuto, segundo;
		StringTokenizer tokens = new StringTokenizer(horaEnvio, ":");
		String aux = tokens.nextToken();
		
		hora = Integer.parseInt(aux);
		aux = tokens.nextToken();
		minuto = Integer.parseInt(aux);
		aux = tokens.nextToken();
		segundo = Integer.parseInt(aux);

		return ((((hora * 60) + minuto) * 60) + segundo) * 1000;
	}
}