package nl.ou.testar.StateModel.Settings;

import org.fruit.alayer.Tag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class AbstractStateSettings extends JDialog {

    private Tag<?>[] allStateTags;
    private Tag<?>[] currentlySelectedStateTags;

    private JLabel label1 = new JLabel("Please choose the widget attributes to use in " +
                                            "creating the abstract state model.");

    private JList list;

    private JButton confirmButton = new JButton("Confirm");

    Window window = SwingUtilities.getWindowAncestor(this);

    public AbstractStateSettings(Tag<?>[] allStateTags, Tag<?>[] currentlySelectedStateTags) {
        this.allStateTags = allStateTags;
        this.currentlySelectedStateTags = currentlySelectedStateTags;
        setSize(800, 600);
        setLayout(null);
        setVisible(true);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // tell the manager to shut down its connection
                super.windowClosing(e);
            }
        });

        init();
    }

    private void init() {
        label1.setBounds(10, 10, 400, 27);
        add(label1);

        list = new JList(allStateTags); //data has type Object[]
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);

        // init the selection based on the currently selected state management tags
        Set<Tag<?>> tagSet = new HashSet<>(Arrays.asList(currentlySelectedStateTags));
        ListModel<Tag<?>> listModel = list.getModel();
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i=0; i < listModel.getSize(); i++) {
            if (tagSet.contains(listModel.getElementAt(i))) {
                selectedIndices.add(i);
            }
        }

        list.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 350));
        listScroller.setBounds(10, 60, 250, 350);
        add(listScroller);

        confirmButton.setBounds(10, 420, 250, 27);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentlySelectedStateTags = ((List<Tag<?>>)list.getSelectedValuesList()).toArray(new Tag<?>[0]);

                dispatchEvent(new WindowEvent(window ,WindowEvent.WINDOW_CLOSING));
                dispose();
            }
        });
        add(confirmButton);
    }

    public Tag<?>[] getCurrentlySelectedStateTags() {
        return currentlySelectedStateTags;
    }

}
