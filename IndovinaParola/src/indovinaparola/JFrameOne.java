/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package indovinaparola;

import javax.naming.InitialContext;
import javax.swing.JFrame;

/**
 *
 * @author becca
 */
public class JFrameOne extends JFrame {

    public JFrameOne() {
        initialize();
    }

    private void initialize() {
        setTitle("Frame one");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
