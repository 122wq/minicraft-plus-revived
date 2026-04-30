package minicraft.core;

import minicraft.network.MinicraftProtocol;
import minicraft.network.NetworkPacket;
import minicraft.network.WorldCreate;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program should allow the client to send it messages. The messages should then 
 * become visible to all other clients.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program creates a new thread for
 * every connection request.
 */
public class Server {

    public static final int LISTENING_PORT = MinicraftProtocol.PORT;
    private static long serverTick = 0L;
    private static final ArrayList<ConnectionHandler> handlers = new ArrayList<>();
    private static WorldCreate serverWorld = new WorldCreate((long)(1000 * Math.random()), 128);



    public static void main(String[] args) 
    {
        new Server();
    }  // end main()

    public Server()
    {
        ServerSocket listener;  // Listens for incoming connections.
        Socket connection;      // For communication with the connecting program.

        /* Accept and process connections forever, or until some error occurs. */

        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            
            while (true) 
            {
                // Accept next connection request and handle it.
                connection = listener.accept();
                
                ConnectionHandler c = new ConnectionHandler(connection);
                c.start();
               
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }
    }
    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    private static class ConnectionHandler extends Thread 
    {
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        int clientNum;
        ConnectionHandler(Socket socket) 
        {
            client = socket;
        }
        public void run() 
        {
            String clientAddress = client.getInetAddress().toString();
	        try 
            {
	            //your code to send messages goes here.
                System.out.println("Connecting");
                NetworkPacket packetFromClient;
                ois = new ObjectInputStream(client.getInputStream());
                oos = new ObjectOutputStream(client.getOutputStream());
                synchronized (handlers) {
                    handlers.add(this);
                    clientNum = handlers.size() - 1;
                }
                handlers.get(handlers.size() - 1).oos.writeObject(new NetworkPacket(MinicraftProtocol.InputType.WORLD_CREATE, serverWorld, MAX_PRIORITY, serverTick, LISTENING_PORT));

                while(true)
                { 
                    Object incoming = ois.readObject();
                    if (incoming instanceof NetworkPacket) {
                        packetFromClient = (NetworkPacket) incoming;
                    } else {
                        packetFromClient = new NetworkPacket(MinicraftProtocol.InputType.NOTIFY, String.valueOf(incoming), clientNum, serverTick, 0);
                    }

                    System.out.println("[" + packetFromClient.getType() + "] " + packetFromClient.getPayload());
                    ArrayList<ConnectionHandler> currentHandlers;
                    synchronized (handlers) {
                        currentHandlers = new ArrayList<ConnectionHandler>(handlers);
                    }
                    for (int i = 0; i < currentHandlers.size(); i++)
                    {
                        ConnectionHandler handler = currentHandlers.get(i);
                        if (handler == this || handler == null || handler.oos == null) {
                            continue;
                        }

                        handler.oos.writeObject(packetFromClient);
                        handler.oos.flush();
                    }
                        
                }
	        }
	        catch (EOFException e)
            {
	            // Client closed the connection; treat this as a nomal disconnect.
	            System.out.println("Client disconnected: " + clientAddress);
	        }
	        catch (Exception e)
            {
	            System.out.println("Error on connection with: " 
	                     + clientAddress + ": " + e);
	        }
            //closes the client 
            finally
            {
                try {
                    if (ois != null) ois.close();
                    if (oos != null) oos.close(); 
                    client.close();
                } catch (Exception e) {
                }
                synchronized (handlers) {
                    handlers.remove(this);
                }

                String disconnectMessage = "Client " + clientNum + " disconnected.";
                ArrayList<ConnectionHandler> currentHandlers;
                synchronized (handlers) {
                    currentHandlers = new ArrayList<ConnectionHandler>(handlers);
                }
                for (int i = 0; i < currentHandlers.size(); i++)
                {
                    try {
                        ConnectionHandler handler = currentHandlers.get(i);
                        if (handler != null && handler.oos != null)
                        {
                            handler.oos.writeObject(new NetworkPacket(MinicraftProtocol.InputType.DISCONNECT, disconnectMessage, clientNum, ++serverTick, 0));
                            handler.oos.flush();
                        }
                    } catch (Exception e) {
                    }
                }
                this.interrupt();
            }
        
        }
    }


}
