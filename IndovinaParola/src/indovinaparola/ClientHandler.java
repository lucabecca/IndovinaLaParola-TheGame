package indovinaparola;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

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

    public ClientHandler(Socket socket, String name) {
        this.socket = socket;
        scan = new Scanner(System.in);
        this.name = name;
        isLosggedIn = true;
        vittoria = false;

        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            log("ClientHander : " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        String received;
        write(output, "Benvenuto " + name + " scrivi \"Pronto\" per ricevere la parola.");

        Server.inserisciParolaDaIndovinare();

        while (Server.finito == false) {
            // log("giocoFinito(): "+Server.giocoFinito());

            received = read();
            if (received.equalsIgnoreCase(LOGOUT)) {
                this.isLosggedIn = false;
                closeSocket();
                closeStreams();
                break;
            }

            forwardToClient(received, Server.parolaAsteriscata);

        }
        closeStreams();
    }

    private void forwardToClient(String received, String parolaAsteriscata) {
        //log("recipient: "+recipient+" message: "+message);

        for (ClientHandler c : Server.getClients()) {
            if (c.isLosggedIn && c.name.equals(this.name)) {
                if (c.tentativiFatti > 0) {
                    log(this.name + " ha provato con: " + received);
                    if(Server.parolaJolly.equals(received.trim()))
                    {
                        parolaAsteriscata = Server.parola;
                        parolaAsteriscata = parolaAsteriscata+"_jolly";
                        log("Il client ha inserito la parola jolly");
                    }
                    else
                    {
                        parolaAsteriscata = Server.controllaCorrettezzaParola(received);
                        c.vittoria = Server.controlloVittoria(parolaAsteriscata);
                    }
                    
                    log("vittoria:  " + c.vittoria);
                    if (c.vittoria == true) {
                        Server.finito = true;
                        write(c.output, "logout");
                        log(this.name + " ha vinto!");
                        break;
                    }
                }
                write(c.output, parolaAsteriscata);

                log(name + " --> " + parolaAsteriscata);
                //log("tentativiFatti: "+c.tentativiFatti);
                c.tentativiFatti++;
                break;

            }
        }
    }

    private String read() {
        String line = "";
        try {
            line = input.readUTF();
        } catch (IOException ex) {
            log("read : " + ex.getMessage());
        }
        return line;
    }

    private void write(DataOutputStream output, String message) {
        try {
            output.writeUTF(message);
        } catch (IOException ex) {
            log("write : " + ex.getMessage());
        }
    }

    private void closeStreams() {
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
