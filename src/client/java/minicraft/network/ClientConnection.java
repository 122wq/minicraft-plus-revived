package minicraft.network;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import minicraft.core.Updater;

public class ClientConnection {
	private InetAddress host;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
	/*
	 * Modify this example so that it opens a dialogue window using java swing, 
	 * takes in a user message and sends it
	 * to the server. The server should output the message back to all connected clients
	 * (you should see your own message pop up in your client as well when you send it!).
	 *  We will build on this project in the future to make a full fledged server based game,
	 *  so make sure you can read your code later! Use good programming practices.
	 *  ****HINT**** you may wish to have a thread be in charge of sending information 
	 *  and another thread in charge of receiving information.
	*/
    
    public ClientConnection()
    {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        try {
            host = InetAddress.getLocalHost();
            socket = new Socket(host.getHostName(), MinicraftProtocol.PORT);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            // Demo packet to prove packet serialization works.
            oos.writeObject(new NetworkPacket(MinicraftProtocol.InputType.LOGIN, "client-connected", 1, Updater.tickCount, 0));
            oos.flush();

            //create the input and output threads
            InputThread i = new InputThread();
            OutputThread o = new OutputThread();
            i.start();
            o.start();
            // wait for the send to complete before closing
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    private class InputThread extends Thread
    {
        String message = "hello";
        boolean pressed;
        public InputThread()
        {
        }
        public synchronized void run()
        {
            try 
            {
                while(true)
                {
                    if (pressed)
                    {
                        NetworkPacket packet = new NetworkPacket(MinicraftProtocol.InputType.NOTIFY, message, 1, Updater.tickCount, 0);
                        oos.writeObject(packet);
                        oos.flush();
                        this.wait(100);
                        pressed = false;
                    }
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class OutputThread extends Thread
    {
        public void run()
        {
            try {
                while(true)
                {
                    Object incoming = ois.readObject();
                    if (incoming instanceof NetworkPacket) {
                        NetworkPacket packet = (NetworkPacket) incoming;
                        System.out.println("[" + packet.getType() + "] " + packet.getPayload());
                    } else {
                        System.out.println(String.valueOf(incoming));
                    }
                    
                }
            }catch (EOFException e){
                System.out.println("Disconnected from Server");
                System.exit(0);
            } 
             catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}