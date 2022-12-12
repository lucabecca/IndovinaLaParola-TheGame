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

    public Client() {
        try {
            // TO DO Lo chiederà all'avvio
            ip = InetAddress.getByName("localhost");
            // Creo la socket che userò per la comunicazione
            socket = new Socket(ip, PORT);

            // Per leggere i dati
            input = new DataInputStream(socket.getInputStream());
            // Per inviare i dati
            output = new DataOutputStream(socket.getOutputStream());

            scan = new Scanner(System.in);

            // Inizializzo logout
            logout = false;

            primoMess = 0;
        } catch (UnknownHostException ex) {
            // Output eccezione
            System.out.println("Client : " + ex.getMessage());
        } catch (IOException ex) {
            // Output eccezione
            System.out.println("Client : " + ex.getMessage());
        }

    }

    public static void main(String[] args) {

        Client client = new Client();
        // Avvio il Thread per la recezione dei messaggi
        client.readMessageThread();
        // Avvio il Thread per l'invio dei messaggi
        client.writeMessageThread();

        // Grafica
        openJFrames();
    }

    private void readMessageThread() {
        // Creo il Thread
        Thread readMessage = new Thread(new Runnable() {

            @Override
            public void run() {
                // Ciclo infinito
                while (!logout) {
                    try {
                        // Leggo il messaggio
                        String msg = input.readUTF();
                        //System.out.println("Sono il client msg: "+msg);
                        // Se il server disconnette per vittoria
                        if (msg.trim().equals("logout")) {
                            logout = true;
                            System.out.println("Hai vinto!\nPremi invio per uscire");
                        } else {
                            // Azione che fa solo al primo messaggio
                            if (primoMess == 0) {
                                System.out.println(msg);
                                primoMess++;
                            } else {
                                //System.out.println("msg: "+msg);
                                if (!msg.contains("jolly")) {
                                    System.out.println("Stato parola: " + msg);
                                } else {
                                    msg = msg.replace("_jolly", "");
                                    System.out.println("La soluzione è: " + msg);
                                    //quando il client riceve la soluzione viene atomaticamente disconnesso
                                    msg = "logout";
                                }

                            }
                        }

                    } catch (IOException ex) {
                        // Output eccezione
                        System.out.println("readMessageThread : " + ex.getMessage());
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
                String msg;
                // Ciclo infinito
                while (!logout) {

                    // Leggo messaggio inserito dall'utente
                    msg = scan.nextLine();

                    // Se l'utente vuole disconnettersi
                    if (msg.trim().equals("logout")) {
                        logout = true;
                    }

                    try {
                        // Invia messaggio
                        output.writeUTF(msg);
                    } catch (IOException ex) {
                        // Output eccezione
                        System.out.println("writeMessageThread : " + ex.getMessage());
                    }
                }
            }
        });
        // Avvio il Thread
        sendMessage.start();
    }

    private static void openJFrames() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Inizializza il frame 1
                JFrameOne frame1 = new JFrameOne();
                frame1.setVisible(true);
            }
        });
    }
}
