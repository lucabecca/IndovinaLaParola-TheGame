/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package indovinaparola;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 * @author becca
 */
public class JFrameMain extends JFrame {

    private String address;

    private JTextField fldIp;

    JTextPane message_field;

    JTextPane room_field;

    String message = "";

    boolean message_is_ready = false;

    public JFrameMain() {
        address = "";
        initialize();
    }

    private void initialize() {
        // Are finesta che chiede l'ip
        OpenDialog();

        OpenChat();
    }

    private void OpenChat() {
        setSize(800, 600);
        setTitle("Chat Indovina la parola");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        room_field = new JTextPane();
        message_field = new JTextPane();
        room_field.setEditable(false);
        ScrollPane x = new ScrollPane();
        x.add(room_field);
        ScrollPane z = new ScrollPane();
        z.add(message_field);
        z.setPreferredSize(new Dimension(100, 100));
        add(x, BorderLayout.CENTER);
        add(z, BorderLayout.SOUTH);

        setVisible(true);
        message_field.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == 10) {
                    message_field.setCaretPosition(0);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == 10 && !message_is_ready) {
                    message = message_field.getText().trim();
                    message_field.setText(null);
                    if (!message.equals(null) && !message.equals("")) {
                        message_is_ready = true;
                    }
                }
            }
        });
    }

    private void OpenDialog() {
        JDialog AddressDialog = new JDialog(this, "Enter server address: ", true);
        AddressDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        AddressDialog.setLocationRelativeTo(null);
        AddressDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        AddressDialog.setSize(500, 100);
        AddressDialog.setResizable(false);

        // JButton btnConnetti
        JButton btnConnetti = creaButtonConnetti();
        btnConnetti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                address = fldIp.getText().trim();
                AddressDialog.dispose();
            }
        });

        // JTextField
        fldIp = creaTextFieldIP();
        AddressDialog.add(fldIp);
        // Add btnConnetti
        AddressDialog.add(btnConnetti);
        fldIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnConnetti.doClick();
            }
        });

        AddressDialog.setVisible(true);
    }

    private JButton creaButtonConnetti() {
        JButton btnConnetti = new JButton("Connetti");

        btnConnetti.setFocusable(false);
        btnConnetti.setToolTipText("Connettiti al server");
        btnConnetti.setFont(new Font("Arial", Font.PLAIN, 18));

        return btnConnetti;
    }

    private JTextField creaTextFieldIP() {
        JTextField fldIp = new JTextField(10);

        fldIp.setToolTipText("IP del server");
        fldIp.setFont(new Font("Arial", Font.PLAIN, 18));

        return fldIp;
    }

    public void displayMessage(String receivedMessage) {
        StyledDocument doc = room_field.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), receivedMessage + "\n", null);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessageReady(boolean messageReady) {
        this.message_is_ready = messageReady;
    }

    public String getAddress() {
        return address;
    }
}
