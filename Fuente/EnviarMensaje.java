package javeriana.edu.co;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;

import javeriana.edu.co.Mensaje;

class EnviarMensaje extends Thread {
	private Thread hilo;
	private String nombreHilo;
	private Mensaje mensaje;
	private int tiempoEnvio;
	private DatagramSocket fuenteSocket;
	private InetAddress ipServidor;
	private int puerto;

	EnviarMensaje(String nombreHilo, String usuario, String cuerpo, 
				  List<String> temas, List<String> infoContexto, 
				  int tiempoEnvio, DatagramSocket fuenteSocket, 
				  InetAddress ipServidor, int puerto) {
		this.hilo = null;
		this.nombreHilo = nombreHilo;
		this.mensaje = new Mensaje (Mensaje.Tipo.NOTICI, cuerpo, usuario);
		this.mensaje.setTemas(temas);
		this.mensaje.setInfoContext(infoContexto);
		this.tiempoEnvio = tiempoEnvio;
		this.fuenteSocket = fuenteSocket;
		this.ipServidor = ipServidor;
		this.puerto = puerto;
	}

	public void setIpServidor(InetAddress newIpServidor) {
		this.ipServidor = newIpServidor;
	}

	public void setPuerto(int newPuerto) {
		this.puerto = newPuerto;
	}

	public int getPuerto() {
		return this.puerto;
	}

	public void run() {
		Calendar tiempoActual = Calendar.getInstance();
		int aux = (((((tiempoActual.get(Calendar.HOUR_OF_DAY) * 60) + 
				  tiempoActual.get(Calendar.MINUTE)) * 60) + 
				  tiempoActual.get(Calendar.SECOND)) * 1000) +
				  tiempoActual.get(Calendar.MILLISECOND);
		int tiempoDormir = this.tiempoEnvio - aux;
		
		try {
			if (tiempoDormir > 0)
				Thread.sleep(tiempoDormir);
			
			ByteArrayOutputStream bStream = new ByteArrayOutputStream ();
			ObjectOutputStream sendData = new ObjectOutputStream (bStream); 
			sendData.writeObject(this.mensaje);
			
			byte[] serializedMessage = bStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket (serializedMessage, 
				serializedMessage.length, ipServidor, puerto);
			fuenteSocket.send(sendPacket);
			
			sendData.close();
			bStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e); 
		}
	}

	public void start() {
		if (this.hilo == null) {
			this.hilo = new Thread (this, this.nombreHilo);
			this.hilo.start();
		}
	}
}