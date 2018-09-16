package javeriana.edu.co;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javafx.util.Pair;
import java.util.Timer;
import java.util.TimerTask;

import javeriana.edu.co.*;
import java.util.Vector;


class MainGestor {

	public static void main(String[] args) {
        Gestor gestor = new Gestor();
		try {
            System.out.println("Digite el modo del gestor");
            System.out.println("  - 0: para backup");
            System.out.println("  - 1: para modo normal");
            String input = System.console().readLine();
            int modo = Integer.parseInt(input);
            System.out.println("Desea añadir broker asociado? (s/n)");
            String opc = System.console().readLine();
            String ipi;
            String pi;
            if(opc=="s\n"){
                System.out.println("Digite ip:");
                ipi = System.console().readLine();
                System.out.println("Digite puerto:");
                pi = System.console().readLine();
                gestor.addBackup(ipi, Integer.parseInt(pi));
            }
            System.out.print("Digite el tamaño maximo de cola");
            input = System.console().readLine();
            int tamMax = Integer.parseInt(input);
            System.out.println(modo+"  " +tamMax);
            
			DatagramSocket serverSocket = new DatagramSocket(6785);
            byte[] receiveData = new byte[1024];
            ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesCliente = new ConcurrentLinkedQueue<>(); 
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaSubscripcionesFuente = new ConcurrentLinkedQueue<>(); 
            ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnviodeMensajes = new ConcurrentLinkedQueue<>();
			ConcurrentLinkedQueue<Pair<Mensaje,Pair<InetAddress,Integer>>> colaEnvioTemasDisponibles = new ConcurrentLinkedQueue<>();
			        
			ClienteThread clienteThread = new ClienteThread(gestor, colaSubscripcionesCliente, modo);
			FuenteThread fuenteThread = new FuenteThread(gestor, colaSubscripcionesFuente, modo);
			PublishMessageThread publishMessageThread = new PublishMessageThread(gestor, colaEnviodeMensajes, serverSocket, modo);
            TopicsThread topicsThread = new TopicsThread(gestor, colaEnvioTemasDisponibles, serverSocket);
            TimeOutThread timeOutThread = new TimeOutThread(gestor,modo,InetAddress.getByName("localhost"),6785);
            ControlThread controlThread = new ControlThread(gestor,modo,InetAddress.getByName("localhost"),6785);

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
                        System.out.println(mensaje);
                        switch(mensaje.getTipo()) {
                            case SUBSCLIE:
                                System.out.println("Esperando subscripción cliente");
                                colaSubscripcionesCliente.add(t2);
                                break;
                            case SUBSFUEN:
                                System.out.println("Esperando subscripción fuente");
                                colaSubscripcionesFuente.add(t2);
                                break;
                            case NOTICI:
                                System.out.println("Esperando backup de noticias");
                                colaEnviodeMensajes.add(t2);
                                break;
                            case UPT:
                                modo=1;
                                for(ClienteGestor client: gestor.getClientes()){
                                    sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),client.getIp(), client.getPuerto());
                                }
                                for(FuenteGestor fuent: gestor.getFuentes()){
                                    sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),fuent.getIp(), fuent.getPuerto());
                                }
                                timeOutThread.start();
                                break;
                            case ERROR:
                                elapsedTime = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                System.out.println(elapsedTime);
                                break;
                            default :
                                System.out.println("Mensaje inválido"); 
                                break;
                        }
                        if(elapsedTime / 1000 > 5){
                            modo=1;
                            for(ClienteGestor client: gestor.getClientes()){
                                sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),client.getIp(), client.getPuerto());
                            }
                            for(FuenteGestor fuent: gestor.getFuentes()){
                                sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),fuent.getIp(), fuent.getPuerto());
                            }
                            timeOutThread.start();
                        }				
                    }
                }
                else{// Modo normal           
                    if (mensaje != null){
                        Pair<InetAddress,Integer> t1= new Pair<>(ipMensaje,puertoMensaje);
                        Pair<Mensaje,Pair<InetAddress,Integer>> t2 = new Pair<>(mensaje,t1);   
                        System.out.println(mensaje);
                        switch(mensaje.getTipo()) {
                            case SUBSCLIE:
                                System.out.println("Esperando subscripción cliente");
                                colaSubscripcionesCliente.add(t2);
                                break;
                            case SUBSFUEN:
                                System.out.println("Esperando subscripción fuente");
                                colaSubscripcionesFuente.add(t2);
                                break;
                            case NOTICI:
                                System.out.println("Esperando envío de noticias");
                                colaEnviodeMensajes.add(t2);
                                break;
                            case TEMAS:
                                System.out.println("Esperando envío de temas");
                                colaEnvioTemasDisponibles.add(t2);
                                break;
                            default :
                                System.out.println("Mensaje inválido"); 
                                break;
                        }				
                    }
                    if(colaEnviodeMensajes.size()>=tamMax||colaEnvioTemasDisponibles.size()>=tamMax
                        ||colaSubscripcionesCliente.size()>=tamMax||colaEnviodeMensajes.size()>=tamMax){
                            if(gestor.getBackups().size()>=1){
                                sendMessage(new Mensaje(Mensaje.Tipo.UPT,"",""),gestor.getBackups().get(0).getKey(), gestor.getBackups().get(0).getValue());
                                timeOutThread.interrupt();
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

    private static void sendMessage(Mensaje mensaje, InetAddress IPAddress, int port){
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            ByteArrayOutputStream bStream  = new ByteArrayOutputStream();
            ObjectOutput sendData = new ObjectOutputStream(bStream);
            sendData.writeObject(mensaje);
            sendData.close();
            byte[] serializedMessage = bStream.toByteArray();;
            DatagramPacket sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, IPAddress, port);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}