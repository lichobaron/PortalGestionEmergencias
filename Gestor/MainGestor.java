package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair;
import java.util.Timer;
import java.util.TimerTask;

import javeriana.edu.co.*;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

@SuppressWarnings("deprecation")
class MainGestor {

	public static void main(String[] args) {
        Gestor gestor;
		try {
            int gestorPort,modo;
            String input;
            System.out.println("        =============   PORTAL DEL GESTIÓN DE EMERGENCIAS   =============");
            System.out.println("Digite el modo del gestor");
            System.out.println("  - 0: para backup");
            System.out.println("  - 1: para modo normal");
            input = System.console().readLine();
            modo = Integer.parseInt(input);
            System.out.print("¿Desea cargar un gestor? (s/n) : ");
            String cargar = System.console().readLine();
            if(cargar.equals("s")){
                System.out.print("Digite el nombre del archivo: ");
                cargar = System.console().readLine();
                ObjectInputStream file = new ObjectInputStream(new FileInputStream(cargar));
                gestor = (Gestor) file.readObject();
                System.out.println("Archivo cargado con éxito!!");
                for(TemaGestor f : gestor.getTemas()){
                    System.out.println(f.toString());
                }
            }else{
                gestor= new Gestor();
                System.out.print("¿Desea añadir broker asociado? (s/n) : ");
                String opc = System.console().readLine();
                String ipi;
                String pi;
                if(opc.equals("s")){
                    System.out.print("Digite ip del broker asociado: ");
                    ipi = System.console().readLine();
                    System.out.print("Digite puerto del broker asociado: ");
                    pi = System.console().readLine();
                    gestor.addBackup(ipi, Integer.parseInt(pi));
                }
            }
            System.out.print("Digite su puerto: ");
            input = System.console().readLine();
            gestorPort = Integer.parseInt(input);
            gestor.setIpGestor(InetAddress.getByName("localhost"));
            gestor.setPuertoGestor(gestorPort);
            System.out.print("Digite el tamaño maximo de cola de mensajes del gestor: ");
            input = System.console().readLine();
            int tamMax = Integer.parseInt(input);
            
			DatagramSocket serverSocket = new DatagramSocket(gestorPort);
            byte[] receiveData = new byte[1024];
            ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente = new ConcurrentLinkedQueue<>(); 
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente = new ConcurrentLinkedQueue<>(); 
            ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes = new ConcurrentLinkedQueue<>();
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnvioTemasDisponibles = new ConcurrentLinkedQueue<>();
			        
			ClienteThread clienteThread = new ClienteThread(gestor, colaSubscripcionesCliente, modo);
			FuenteThread fuenteThread = new FuenteThread(gestor, colaSubscripcionesFuente, modo);
			PublishMessageThread publishMessageThread = new PublishMessageThread(gestor, colaEnviodeMensajes, serverSocket, modo);
            TopicsThread topicsThread = new TopicsThread(gestor, colaEnvioTemasDisponibles, serverSocket);
            TimeOutThread timeOutThread = new TimeOutThread(gestor,modo,InetAddress.getByName("localhost"),gestorPort);
            ControlThread controlThread = new ControlThread(gestor,modo,InetAddress.getByName("localhost"),gestorPort);

			clienteThread.start();
			fuenteThread.start();
			publishMessageThread.start();
            topicsThread.start();
            
            if(modo==0){
                controlThread.start();
            }
            if (modo==1){
                timeOutThread.start();
            }
             
            long startTime=0;
            long elapsedTime=0;
            if(modo==0){
                startTime = System.currentTimeMillis();
            }
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InetAddress ipMensaje = receivePacket.getAddress();
                int puertoMensaje = receivePacket.getPort();
                byte[] data = receivePacket.getData();
                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
                Mensaje mensaje = (Mensaje)iStream.readObject();
                iStream.close();

                if(modo == 0){//Modo backup
                    if (mensaje != null){
                        Pair<InetAddress,Integer> t1= new Pair<>(mensaje.getIp(),mensaje.getPuerto());
                        Pair<Mensaje,Pair<InetAddress,Integer>> t2 = new Pair<>(mensaje,t1);   
                        switch(mensaje.getTipo()) {
                            case SUBSCLIE:
                                System.out.println("Recepción de suscripción de cliente...");
                                colaSubscripcionesCliente.add(t2);
                                elapsedTime = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                break;
                            case SUBSFUEN:
                                System.out.println("Recepción de suscripción de fuente...");
                                colaSubscripcionesFuente.add(t2);
                                elapsedTime = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                break;
                            case NOTICI:
                                System.out.println("Recepción de backup de noticias...");
                                colaEnviodeMensajes.add(t2);
                                elapsedTime = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                break;
                            case UPT:
                                modo=1;
                                for(ClienteGestor client: gestor.getClientes()){

                                    sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),client.getIp(), client.getPuerto(), serverSocket);
                                }
                                for(FuenteGestor fuent: gestor.getFuentes()){
                                    sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),fuent.getIp(), fuent.getPuerto(), serverSocket);
                                }
                                for(ClienteGestor c : gestor.getClientes()){
                                    System.out.println(c.toString());
                                }
                                clienteThread.setModo(modo);
                                fuenteThread.setModo(modo);
                                publishMessageThread.setModo(modo);
                                controlThread.stop();
                                timeOutThread.start();
                                elapsedTime = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                break;
                            case ERROR:
                                elapsedTime = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                break;
                            default :
                                System.out.println("Mensaje inválido - Gestor Backup"); 
                                break;
                        }
                        if(elapsedTime / 1000 > 5){
                            modo=1;
                            for(ClienteGestor client: gestor.getClientes()){
                                sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),client.getIp(), client.getPuerto(), serverSocket);
                            }
                            for(FuenteGestor fuent: gestor.getFuentes()){
                                sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),fuent.getIp(), fuent.getPuerto(), serverSocket);
                            }
                            controlThread.stop();
                            timeOutThread.start();
                            clienteThread.setModo(modo);
                            fuenteThread.setModo(modo);
                            publishMessageThread.setModo(modo);                           
                        }				
                    }
                }
                else{// Modo normal           
                    if (mensaje != null){
                        Pair<InetAddress,Integer> t1= new Pair<>(ipMensaje,puertoMensaje);
                        Pair<Mensaje,Pair<InetAddress,Integer>> t2 = new Pair<>(mensaje,t1);   
                        switch(mensaje.getTipo()) {
                            case SUBSCLIE:
                                System.out.println("Recepción de suscripción de cliente...");
                                colaSubscripcionesCliente.add(t2);
                                break;
                            case SUBSFUEN:
                                System.out.println("Recepción de suscripción de fuente...");
                                colaSubscripcionesFuente.add(t2);
                                break;
                            case NOTICI:
                                System.out.println("Recepción información para envío de noticias...");
                                colaEnviodeMensajes.add(t2);
                                break;
                            case TEMAS:
                                System.out.println("Enviando lista de temas...");
                                colaEnvioTemasDisponibles.add(t2);
                                break;
                            default :
                                System.out.println("Mensaje inválido - Gestor Normal"); 
                                break;
                        }				
                    }
                    if(colaEnviodeMensajes.size()>=tamMax||colaEnvioTemasDisponibles.size()>=tamMax
                        ||colaSubscripcionesCliente.size()>=tamMax||colaEnviodeMensajes.size()>=tamMax){
                            if(gestor.getBackups().size()>=1){
                                sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),gestor.getBackups().get(0).getKey(), gestor.getBackups().get(0).getValue(), serverSocket);
                                timeOutThread.stop();
                            }
                            else{
                                System.out.println("No hay backups!");
                            }                           
                    }
                }
			}
 
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void sendMessage(Mensaje mensaje, InetAddress IPAddress, int port, DatagramSocket clientSocket){
        try {
            ByteArrayOutputStream bStream  = new ByteArrayOutputStream();
            ObjectOutput sendData = new ObjectOutputStream(bStream);
            sendData.writeObject(mensaje);
            sendData.close();
            byte[] serializedMessage = bStream.toByteArray();;
            DatagramPacket sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, port);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}