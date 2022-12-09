/*
Indovina la parola  (usare gitHub):
il Server gestisce oltre le connessioni anche
la scelta della parola segreta.
********

i Client hanno il compito di indovinare 

la parola



abcdefgh



riceve: ********

oppure  a*****g*

		abzzzzgz

		ab****g*



le parole da indovinare si trovano su un file 

di testo  (lunghezza fissa o variabile)



Ciascun client conosce il numero di 

lettere che compongono la parolaSegreta, 

dopo ogni tentativo riceve l'esito , se ci sono lettere giuste 

al posto giusto le visualizza.

es. parolaSegreta="casa" tentativo="cena"

    allora visualizzo "c**a"



*/
package indovinaparola;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private int NTENTATIVI = 5;

    
    private static Socket socket; //porta su cui avviene la conversazione
    private static ServerSocket serverSocket;
    static int numOfUsers = 0; //variabile per gestire pi client
    static List<ClientHandler> clients; //lista di client connessi al server
    
    
    private static boolean giocoFinito()
    {
        //restituisce true se il gioco è finito altrimenti false;
        //il gioco finisce se si supera NTENTATIVI
        return false;
    }
    
    
    private static void waitConnection() throws IOException, ClassNotFoundException {
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
            
            //leggere dalla socket lo stream inviato dal client
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //converto l'oggetto inviato dal client in una stringa
            String message = (String) ois.readObject();
            System.out.println("Message Received: " + message); //questo message sarà quello che il client ha inviato al server
            
            
            //visualizzo l'ip del client che mi ha contattato
            //se lo fai in localhost 127.0.0.1
            System.out.println("Client accepted : " + socket.getInetAddress());
            numOfUsers++;

            ClientHandler handler = new ClientHandler(socket, "user" + numOfUsers);

            Thread thread = new Thread(handler);
            addClient(handler);
            thread.start();
            
            //creo lo stream per rispondere al client
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket
            oos.writeObject("Hi Client "+message);
            
            
            ois.close();
            oos.close();
            socket.close();
            
            if(giocoFinito()==true)
            {
                break; //server per uscire dal cliclo infinito che tiene in vita il server
                        
            }
        }
        //chiude la socket del server
        System.out.println("Server spento.");
        serverSocket.close();
    }
    
    public static List<ClientHandler> getClients() {
        return clients;
    }

    //metodo che aggiunge alla lista clients il nuovo client che ha contattato il server
    private static void addClient(ClientHandler client) {
        clients.add(client);
    }
    
    
    //metodo che invia un messaggio al client
    private static void invioAlClient(String parolaAsteriscata, String stringaRicevuta) {
        
        //dentro stringaRicevuta c'è il nome del client e la stringa che il cliente vuole provare come parola     

        //stringaRicevuta="numclient_parolaProvata"
        //estraggo dalla stringaRicevuta il numclient e parolaProvata e testo se è ok
        String numClient="";
        String parolaProvata="";
        
        //scorre la lista dei client che hanno contattato il server 
        for(ClientHandler c : Server.getClients()){
            //se il client c è connesso e il suo nome equivale a quello che lo ha contattato all'inizio
            if(c.isLosggedIn && c.name.equals(stringaRicevuta))
            {
                //scrive il messaggio
                //write(c.getOutput(),  recipient + " : " + message);
                //System.out.println(c.name + " --> " + recipient + " : " + message);
                break;
            }
        }
    }
    
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        waitConnection(); //richiamo il mettodo che avvia la socket del server e aspetta le request dei client
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Inserisci la parola da indovinare: ");
        parola = scan.nextLine();
        
        //una volta generata la parola invio un msg al client con *** che hanno la stessa lunghezza della parola
       String parolaAsterischi="";
       for(int i=0; i<parola.length(); i++)
       {
           parolaAsterischi = parolaAsterischi+"*";
       }
        String stringaRicevuta="";
       
       invioAlClient(parolaAsterischi, stringaRicevuta);
    }
    
}
