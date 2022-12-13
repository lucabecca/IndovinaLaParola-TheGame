package indovinaparola;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    public static final String LOGOUT = "logout";
    public static final int PORT = 1234;

    final Socket socket;
    final Scanner scan;
    String name;
    boolean isLosggedIn;
    int tentativiFatti = 0;
    boolean vittoria;

    private DataInputStream input;
    private DataOutputStream output;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsLosggedIn() {
        return isLosggedIn;
    }

    public void setIsLosggedIn(boolean isLosggedIn) {
        this.isLosggedIn = isLosggedIn;
    }

    public DataInputStream getInput() {
        return input;
    }

    public void setInput(DataInputStream input) {
        this.input = input;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public void setOutput(DataOutputStream output) {
        this.output = output;
    }

    public ClientHandler(Socket socket, String name) {//costruttore
        this.socket = socket;//collegamento col server
        scan = new Scanner(System.in);
        this.name = name;
        isLosggedIn = true;//dopo aver creato il nome diventa true
        vittoria = false;

        try {
            //grazie alla socket(collegamento)il client puo scambiare un input
            //e output con il server
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            log("ClientHander : " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        String received;
        write(output, "Benvenuto " + name + ".");

        while (isLosggedIn == true) {//se il server è attivo
            // log("giocoFinito(): "+Server.giocoFinito());

            received = read(); //il server riceve un messaggio dal client
            if (received.equalsIgnoreCase(LOGOUT)) {//per uscire e "interrompere"
                this.isLosggedIn = false;
                closeSocket();
                closeStreams();
                break;
            }

            //invia al client
            forwardToClient(received, Server.parolaAsteriscata);

        }
        //closeStreams();
    }

    private void forwardToClient(String received, String parolaAsteriscata) {
        //log("recipient: "+recipient+" message: "+message);

        for (ClientHandler c : Server.getClients()) {
            if (c.isLosggedIn && c.name.equals(this.name)) {
                if (c.tentativiFatti > 0) {
                    log(this.name + " ha provato con: " + received);
                    if (Server.parolaJolly.equals(received.trim())) {//se ricevo jolly
                        parolaAsteriscata = Server.parola;//da la soluzione
                        parolaAsteriscata = parolaAsteriscata + "_jolly";
                        log("Il client ha inserito la parola jolly");
                    } else {
                        //controlla se il client ha scritto delle lettere corrette
                        //e visualizza le lettere azzeccate
                        parolaAsteriscata = Server.controllaCorrettezzaParola(received);
                        //richiama il metodo che controlla gli asterischi, se non ci sono la parola è indovinata
                        c.vittoria = Server.controlloVittoria(parolaAsteriscata);
                    }

                    if (c.vittoria == true) {
                        Server.finito = true;
                        isLosggedIn=false;
                        write(c.output, "logout");
                        log(this.name + " ha vinto!");
                        break;
                    }
                }
                write(c.output, name + ":" + parolaAsteriscata);

                log(name + " --> " + parolaAsteriscata);
                //log("tentativiFatti: "+c.tentativiFatti);
                c.tentativiFatti++;
                break;

            }
        }
    }

    private String read() {//legge l'input da inviare
        String line = "";
        try {
            line = input.readUTF();
        } catch (IOException ex) {
            log("read : " + ex.getMessage());
            isLosggedIn=false;
        }
        return line;
    }

    private void write(DataOutputStream output, String message) {//scrive l'output ricevuto
        try {
            output.writeUTF(message);
        } catch (IOException ex) {
            log("write : " + ex.getMessage());
            isLosggedIn=false;
        }
    }

    private void closeStreams() {//chiude lo Streams l'input e Streams l'output
        try {
            this.input.close();
            this.output.close();
        } catch (IOException ex) {
            log("closeStreams : " + ex.getMessage());
        }
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException ex) {
            log("closeSocket : " + ex.getMessage());
        }
    }

    private void log(String msg) {
        System.out.println(msg);
    }

}
