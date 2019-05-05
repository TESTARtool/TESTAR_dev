package nl.ou.testar.StateModel;

import javax.swing.*;
import java.awt.*;

public class QueueVisualizer extends JDialog {

    private Label msg;

    public QueueVisualizer(String title) {
        super((JFrame)null, title, false);
        this.setType(Type.POPUP);
        this.setUndecorated(true);
        msg = new Label(title);
        msg.setBackground(Color.BLACK);
        msg.setForeground(Color.WHITE);
        this.add(msg);
        int dimW = (title.length() + 1) * 12;
        this.setSize(new Dimension(dimW > 512 ? 512 : dimW, 32));
        this.setEnabled(false);
        this.setOpacity(0.75f);
        this.setVisible(true);
    }

    public void updateMessage(String message) {
        msg.setText(message);
        msg.repaint();
    }

    public void stop() {
        this.setVisible(false);
        this.dispose();
    }
}
