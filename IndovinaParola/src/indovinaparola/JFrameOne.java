/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package indovinaparola;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author becca
 */
public class JFrameOne extends JFrame {

    // JButton btnConnetti
    private JButton btnConnetti;

    public JFrameOne() {
        initialize();
    }

    private void initialize() {
        setTitle("Frame one");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 100);
        setLocationRelativeTo(null);

        // JPanel
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // JLabel
        JLabel lbl = new JLabel("Inserisci l'IP del server:");
        lbl.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(lbl);

        // JTextField
        JTextField fldIp = creaTextFieldIP();
        panel.add(fldIp);

        // JButton btnConnetti
        btnConnetti = creaButtonConnetti();
        panel.add(btnConnetti);

        // add frame
        add(panel, BorderLayout.CENTER);
    }

    private JButton creaButtonConnetti() {
        JButton btnConnetti = new JButton("Connetti");

        btnConnetti.setFocusable(false);
        btnConnetti.setToolTipText("Connettiti al server");
        btnConnetti.setFont(new Font("Arial", Font.PLAIN, 18));

        btnConnetti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        return btnConnetti;
    }

    private JTextField creaTextFieldIP() {
        JTextField fldIp = new JTextField(10);

        fldIp.setToolTipText("IP del server");
        fldIp.setFont(new Font("Arial", Font.PLAIN, 18));

        fldIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnConnetti.doClick();
            }
        });

        return fldIp;
    }
}
