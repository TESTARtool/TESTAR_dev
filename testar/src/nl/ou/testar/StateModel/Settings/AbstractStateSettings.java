package nl.ou.testar.StateModel.Settings;

import es.upv.staq.testar.StateManagementTags;
import org.fruit.alayer.Tag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class AbstractStateSettings extends JDialog {

    private Tag<?>[] allStateTags;
    private Tag<?>[] currentlySelectedStateTags;
    private Tag<?>[] defaultTags;

    private JLabel label1 = new JLabel("Please choose the widget attributes to use in " +
                                            "creating the abstract state model.");
    private JLabel label2 = new JLabel("General attributes");
    private JLabel label3 = new JLabel("Control patterns");

    private JList generalList;
    private JList controlPatternList;

    private JButton confirmButton = new JButton("Confirm");
    private JButton resetToDefaultsButton = new JButton("Reset to defaults");

    Window window = SwingUtilities.getWindowAncestor(this);

    public AbstractStateSettings(Tag<?>[] allStateTags, Tag<?>[] currentlySelectedStateTags, Tag<?>[] defaultTags) {
        this.allStateTags = Arrays.stream(allStateTags).sorted(Comparator.comparing(Tag::name)).toArray(Tag<?>[]::new);
        this.currentlySelectedStateTags = currentlySelectedStateTags;
        this.defaultTags = defaultTags;
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

        /////// GENERAL STATE MANAGEMENT TAGS ////////
        label2.setBounds(10, 50, 250, 27);

        generalList = new JList(Arrays.stream(allStateTags).filter(tag -> StateManagementTags.getTagGroup(tag).equals(StateManagementTags.Group.General)).toArray()); //data has type Object[]
        generalList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        generalList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        generalList.setVisibleRowCount(-1);

        JScrollPane listScrollerGeneral = new JScrollPane(generalList);
        listScrollerGeneral.setPreferredSize(new Dimension(250, 350));
        listScrollerGeneral.setBounds(10, 80, 250, 350);
        add(listScrollerGeneral);

        ///////// CONTROL PATTERN STATE MANAGEMENT TAGS /////////
        label3.setBounds(450, 50, 250, 27);

        controlPatternList = new JList(Arrays.stream(allStateTags).filter(tag -> StateManagementTags.getTagGroup(tag).equals(StateManagementTags.Group.ControlPattern)).toArray()); //data has type Object[]
        controlPatternList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        controlPatternList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        controlPatternList.setVisibleRowCount(-1);

        JScrollPane listScrollerControlPattern = new JScrollPane(controlPatternList);
        listScrollerControlPattern.setPreferredSize(new Dimension(250, 350));
        listScrollerControlPattern.setBounds(450, 80, 250, 350);
        add(listScrollerControlPattern);

        // init the selection based on the currently selected state management tags
        populateLists();

        /////////// CONFIRM BUTTON ////////////
        confirmButton.setBounds(10, 440, 250, 27);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentlySelectedStateTags = (Tag<?>[]) Stream.concat(generalList.getSelectedValuesList().stream(), controlPatternList.getSelectedValuesList().stream())
                        .toArray(Tag<?>[]::new);


                dispatchEvent(new WindowEvent(window ,WindowEvent.WINDOW_CLOSING));
                dispose();
            }
        });
        add(confirmButton);

        ///////////// DEFAULTS BUTTON //////////////////
        resetToDefaultsButton.setBounds(275, 440, 250, 27);
        resetToDefaultsButton.addActionListener(e -> {
            currentlySelectedStateTags = defaultTags;
            populateLists();
        });
        add(resetToDefaultsButton);
    }

    public Tag<?>[] getCurrentlySelectedStateTags() {
        return currentlySelectedStateTags;
    }

    private void populateLists() {
        Set<Tag<?>> tagSet = new HashSet<>(Arrays.asList(currentlySelectedStateTags));
        ListModel<Tag<?>> listModel = generalList.getModel();
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i=0; i < listModel.getSize(); i++) {
            if (tagSet.contains(listModel.getElementAt(i))) {
                selectedIndices.add(i);
            }
        }

        generalList.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());

        listModel = controlPatternList.getModel();
        selectedIndices = new ArrayList<>();
        for (int i=0; i < listModel.getSize(); i++) {
            if (tagSet.contains(listModel.getElementAt(i))) {
                selectedIndices.add(i);
            }
        }

        controlPatternList.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());
    }

}
