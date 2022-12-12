/*
Indovina la parola  (usare gitHub):
il Server gestisce oltre le connessioni anche
la scelta della parola segreta.
********

i Client hanno il compito di indovinare la parola
abcdefgh
riceve: ********
oppure  a*****g*
abzzzzgz
ab****g*
le parole da indovinare si trovano su un file 
di testo  (lunghezza fissa o variabile)
Ciascun client conosce il numero di lettere che compongono la parolaSegreta, 
dopo ogni tentativo riceve l'esito , se ci sono lettere giuste al posto giusto le visualizza.

es. parolaSegreta="casa" tentativo="cena"
allora visualizzo "c**a"
 */
package indovinaparola;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Server {

    private static String parola;

    static String parolaAsteriscata = "";

    static int NTENTATIVI = 5;
    static int tentativiFatti = 0;
    static boolean finito = false;

    static List<ClientHandler> clients;

    static boolean controlloVittoria(String parolaAsteriscata) {
        //se la parola asteriscata non contiene più nessun asterisco significa che è stata scoperta

        if (parolaAsteriscata.contains("*")) {
            finito = false;
            return false;
        } else {
            finito = true;
            return true;
        }

    }
    ServerSocket serverSocket;
    static int numOfUsers = 0;
    Socket socket;

    //costruttore del server che inizializza la sua socket
    public Server() {
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(Constants.PORT);
        } catch (IOException ex) {
            log("Server : " + ex.getMessage());
        }
    }

    /*metodo che controlla quali lettere ricevute sono corrette.
    Se sono corrette sostituisce l'asterisco con la lettera
     */
    static String controllaCorrettezzaParola(String received) {
        //log("ricevuta: "+received);
        //log("recipient: "+recipient+" message: "+message);

        //faccio il confronto carattere per carattere solo se le lunghezze corrispondono
        if (received.length() == parola.length()) {
            //converto la parola asteriscata in un array di caratteri così è più easy manipolarla dopo
            char[] parolaSvelata = parolaAsteriscata.toCharArray();
            // log("parola: "+parola);

            //scorro tutta la stringa ricevuta
            for (int i = 0; i < received.length(); i++) {
                //confronto carattere con carattere se uguale scopro la lettera che prima era asteriscata
                if (received.charAt(i) == parola.charAt(i)) {
                    //per ogni cella i che corrisponde scopro la lettera originale
                    parolaSvelata[i] = parola.charAt(i);
                }
            }
            //ricompongo l'array di char in String
            parolaAsteriscata = String.valueOf(parolaSvelata);
        }

        tentativiFatti++;
        return parolaAsteriscata;
    }

    static void inserisciParolaDaIndovinare() {

        Scanner scan = new Scanner(System.in);
        System.out.println("Inserisci la parola da indovinare: ");
        parola = scan.nextLine();

        //una volta generata la parola invio un msg al client con *** che hanno la stessa lunghezza della parola
        for (int i = 0; i < parola.length(); i++) {
            parolaAsteriscata = parolaAsteriscata + "*";
        }

    }

    private void waitConnection() {
        log("Server Running...");

        while (Server.finito == false) {
            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                log("waitConnection : " + ex.getMessage());
            }

            log("Client accepted : " + socket.getInetAddress());
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
    private static void addClient(ClientHandler client) {
        clients.add(client);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Server server = new Server();
        server.waitConnection();

    }

    private static void log(String message) {
        System.out.println(message);
    }

}
