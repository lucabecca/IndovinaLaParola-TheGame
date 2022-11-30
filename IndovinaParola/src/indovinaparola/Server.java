/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package indovinaparola;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author Giordano
 */
public class Server {
    
    private static String parola;

    
    Socket socket; //porta su cui avviene la conversazione
    ServerSocket serverSocket;
    static int numOfUsers = 0; //variabile per gestire pi client
    static List<ClientHandler> clients; //lista di client connessi al server
    
    private void waitConnection() {
        System.out.println("Server Running...");

        //faccio un ciclo infinito di accettazione/ascolto di nuovi client
        while (true) {
            try 
            {
                socket = serverSocket.accept();
            } catch (IOException ex) 
            {
                System.out.println("waitConnection : " + ex.getMessage());
            }
            
            //visualizzo l'ip del client che mi ha contattato
            //se lo fai in localhost 127.0.0.1
            System.out.println("Client accepted : " + socket.getInetAddress());
            numOfUsers++;

            ClientHandler handler = new ClientHandler(socket, "user" + numOfUsers);

            Thread thread = new Thread(handler);
            addClient(handler);
            thread.start();
        }
    }
    
    public static List<ClientHandler> getClients() {
        return clients;
    }

    //metodo che aggiunge alla lista clients il nuovo client che ha contattato il server
    private void addClient(ClientHandler client) {
        clients.add(client);
    }
    
    //il metodo prende in ingresso un pacchetto output e il messaggio da inviare
    private static void write(DataOutputStream output , String message){
        try {
            output.writeUTF(message); //scrive con una certa codifica UTF la stringa da inviare dentro il pacchetto di output
        } catch (IOException ex) {
            System.out.println("write : " + ex.getMessage());
        }
    }
    
    
    //metodo che invia un messaggio al client
    private static void invioAlClient(String msg) {
        // username # message
        StringTokenizer tokenizer = new StringTokenizer(msg, "#");
        String recipient = tokenizer.nextToken().trim();
        String message = tokenizer.nextToken().trim();
        
        //scorre la lista dei client che hanno contattato il server 
        for(ClientHandler c : Server.getClients()){
            //se il client c è connesso e il suo nome equivale a quello che lo ha contattato all'inizio
            if(c.isLosggedIn && c.name.equals(recipient))
            {
                //scrive il messaggio
                write(c.getOutput(),  recipient + " : " + message);
                System.out.println(c.name + " --> " + recipient + " : " + message);
                break;
            }
        }
    }
    
    
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Inserisci la parola da indovinare: ");
        parola = scan.nextLine();
        
        //una volta generata la parola invio un msg al client con *** che hanno la stessa lunghezza della parola
       String parolaAsterischi="";
       for(int i=0; i<parola.length(); i++)
       {
           parolaAsterischi = parolaAsterischi+"*";
       }
       
       invioAlClient(parolaAsterischi);
    }
    
}
