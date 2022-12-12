/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package indovinaparola;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author becca
 */
public class Client {

    private Scanner scan;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private InetAddress ip;
    private int PORT = 1234;

    private boolean logout;

    private int primoMess;

    private String Address = "";

    private JFrameMain frame;

    public Client() {
        // Grafica
        openJFrames();

        scan = new Scanner(System.in);

        // Inizializzo logout
        logout = false;

        primoMess = 0;
    }

    public static void main(String[] args) {
        Client client = new Client();
    }

    private void readMessageThread() {
        // Creo il Thread
        Thread readMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                while (!logout) {
                    try {
                        // Leggo il messaggio
                        String msg = input.readUTF();
                        // Se il server disconnette per vittoria
                        if (msg.trim().equals("logout")) {
                            logout = true;
                            System.out.println("Hai vinto!\nPremi la X per uscire");
                            frame.displayMessage("Hai vinto!\nPremi la X per uscire");
                        } else {
                            // Se l'indirizzo è diverso da vuoto, allora può iniziare a ricevere messaggi
                            if (!Address.equals("")) {
                                if (!msg.contains("jolly")) {
                                    System.out.println(msg);
                                    frame.displayMessage(msg);
                                } else {
                                    msg = msg.replace("_jolly", "");
                                    System.out.println( msg);
                                    frame.displayMessage( msg);
                                    //quando il client riceve la soluzione viene atomaticamente disconnesso
                                    msg = "logout";
                                }

                            }
                        }
                    } catch (IOException ex) {
                        // Output eccezione
                        System.out.println("readMessageThread : " + ex.getMessage());
                        logout = true;
                    }
                }
            }
        });
        // Avvio il Thread
        readMessage.start();
    }

    private void writeMessageThread() {
        Thread sendMessage = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("Inserire \"logout\" per disconnettersi.");
                String msg = "";

                while (!logout) {
                    // Azione che fa solo al primo messaggio
                    if (primoMess == 0 && !Address.equals("")) {
                        msg = "pronto";
                        frame.displayMessage("Inserire \"logout\" per disconnettersi.");
                        primoMess++;
                    } else if (primoMess > 0) {
                        while (!frame.message_is_ready) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        // Leggo messaggio inserito dall'utente dal frame
                        msg = frame.getMessage();
                        frame.setMessageReady(false);
                    }

                    // Se l'utente vuole disconnettersi
                    if (msg.trim().equals("logout")) {
                        logout = true;
                    }

                    try {
                        if (!msg.equals("") && !Address.equals("")) {
                            // Invia messaggio
                            output.writeUTF(msg);
                        }
                    } catch (IOException ex) {
                        // Output eccezione
                        System.out.println("writeMessageThread : " + ex.getMessage());
                        logout = true;
                    }
                }
            }
        });
        // Avvio il Thread
        sendMessage.start();
    }

    private void openJFrames() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Inizializza il frame
                frame = new JFrameMain();
                Address = frame.getAddress();
                System.out.println("Address:" + Address);
                connectServer();
            }
        });
    }

    private void connectServer() {
        try {
            ip = InetAddress.getByName(Address);
            // Creo la socket che userò per la comunicazione
            socket = new Socket(ip, PORT);

            // Per leggere i dati
            input = new DataInputStream(socket.getInputStream());
            // Per inviare i dati
            output = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {
            // Output eccezione
            System.out.println("Client : " + ex.getMessage());
        } catch (IOException ex) {
            // Output eccezione
            System.out.println("Client : " + ex.getMessage());
        }

        // Avvio il Thread per la recezione dei messaggi
        readMessageThread();
        // Avvio il Thread per l'invio dei messaggi
        writeMessageThread();
    }
}
