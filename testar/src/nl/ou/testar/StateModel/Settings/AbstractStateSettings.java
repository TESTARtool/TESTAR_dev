package nl.ou.testar.StateModel.Settings;

import es.upv.staq.testar.ActionManagementTags;
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
    private Tag<?>[] defaultStateTags;

    private Tag<?>[] allActionTags;
    private Tag<?>[] currentlySelectedActionTags;
    private Tag<?>[] defaultActionTags;

    private JLabel label1 = new JLabel("Please choose the widget attributes to use in "
    + "creating the abstract state model. Control + Left Click");
    private JLabel label2 = new JLabel("General attributes");
    private JLabel label3 = new JLabel("Control patterns");
    private JLabel label4 = new JLabel("WebDrivers attributes");
    private JLabel label5 = new JLabel("Action attributes");

    private JList generalList;
    private JList controlPatternList;
    private JList webdriverList;
    private JList generalActionList;

    private JButton confirmButton = new JButton("Confirm");
    private JButton resetToDefaultsButton = new JButton("Reset to defaults");

    Window window = SwingUtilities.getWindowAncestor(this);

    public AbstractStateSettings(Tag<?>[] allStateTags, Tag<?>[] currentlySelectedStateTags, Tag<?>[] defaultStateTags, 
            Tag<?>[] allActionTags, Tag<?>[] currentlySelectedActionTags, Tag<?>[] defaultActionTags) {
        // State Tags
        this.allStateTags = Arrays.stream(allStateTags).sorted(Comparator.comparing(Tag::name)).toArray(Tag<?>[]::new);
        this.currentlySelectedStateTags = currentlySelectedStateTags;
        this.defaultStateTags = defaultStateTags;
        // Action Tags
        this.allActionTags = Arrays.stream(allActionTags).sorted(Comparator.comparing(Tag::name)).toArray(Tag<?>[]::new);
        this.currentlySelectedActionTags = currentlySelectedActionTags;
        this.defaultActionTags = defaultActionTags;
        setSize(1000, 800);
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
        label1.setBounds(10, 10, 600, 27);
        add(label1);

        /////// GENERAL STATE MANAGEMENT TAGS ////////
        label2.setBounds(10, 50, 250, 27);
        add(label2);

        generalList = new JList(Arrays.stream(allStateTags).filter(tag -> StateManagementTags.getTagGroup(tag).equals(StateManagementTags.Group.General)).toArray()); //data has type Object[]
        generalList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        generalList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        generalList.setVisibleRowCount(-1);

        JScrollPane listScrollerGeneral = new JScrollPane(generalList);
        listScrollerGeneral.setPreferredSize(new Dimension(250, 350));
        listScrollerGeneral.setBounds(10, 80, 250, 350);
        add(listScrollerGeneral);

        ///////// CONTROL PATTERN STATE MANAGEMENT TAGS /////////
        label3.setBounds(360, 50, 250, 27);
        add(label3);

        controlPatternList = new JList(Arrays.stream(allStateTags).filter(tag -> StateManagementTags.getTagGroup(tag).equals(StateManagementTags.Group.ControlPattern)).toArray()); //data has type Object[]
        controlPatternList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        controlPatternList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        controlPatternList.setVisibleRowCount(-1);

        JScrollPane listScrollerControlPattern = new JScrollPane(controlPatternList);
        listScrollerControlPattern.setPreferredSize(new Dimension(250, 350));
        listScrollerControlPattern.setBounds(360, 80, 250, 350);
        add(listScrollerControlPattern);
        
        ///////// WEB DRIVER STATE MANAGEMENT TAGS /////////
        label4.setBounds(710, 50, 250, 27);
        add(label4);
        
        webdriverList = new JList(Arrays.stream(allStateTags).filter(tag -> StateManagementTags.getTagGroup(tag).equals(StateManagementTags.Group.WebDriver)).toArray()); //data has type Object[]
        webdriverList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        webdriverList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        webdriverList.setVisibleRowCount(-1);

        JScrollPane listScrollerWebdriver = new JScrollPane(webdriverList);
        listScrollerWebdriver.setPreferredSize(new Dimension(250, 350));
        listScrollerWebdriver.setBounds(710, 80, 250, 350);
        add(listScrollerWebdriver);
        
        ///////// ACTION MANAGEMENT TAGS /////////
        label5.setBounds(360, 450, 250, 27);
        add(label5);

        generalActionList = new JList(Arrays.stream(allActionTags).filter(tag -> ActionManagementTags.getTagGroup(tag).equals(ActionManagementTags.Group.GeneralAction)).toArray()); //data has type Object[]
        generalActionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        generalActionList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        generalActionList.setVisibleRowCount(-1);

        JScrollPane listScrollerAction = new JScrollPane(generalActionList);
        listScrollerAction.setPreferredSize(new Dimension(250, 250));
        listScrollerAction.setBounds(360, 480, 250, 250);
        add(listScrollerAction);

        // init the selection based on the currently selected state management tags
        populateLists();

        /////////// CONFIRM BUTTON ////////////
        confirmButton.setBounds(10, 480, 250, 27);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	Stream allListConcatenated = Stream.concat(
            			Stream.concat(generalList.getSelectedValuesList().stream(),controlPatternList.getSelectedValuesList().stream()),
            			webdriverList.getSelectedValuesList().stream());
            	
                currentlySelectedStateTags = (Tag<?>[]) allListConcatenated.toArray(Tag<?>[]::new);

                // Action Tags List
                Stream actionList = generalActionList.getSelectedValuesList().stream();
                currentlySelectedActionTags = (Tag<?>[]) actionList.toArray(Tag<?>[]::new);

                dispatchEvent(new WindowEvent(window ,WindowEvent.WINDOW_CLOSING));
                dispose();
            }
        });
        add(confirmButton);

        ///////////// DEFAULTS BUTTON //////////////////
        resetToDefaultsButton.setBounds(10, 520, 250, 27);
        resetToDefaultsButton.addActionListener(e -> {
            currentlySelectedStateTags = defaultStateTags;
            currentlySelectedActionTags = defaultActionTags;
            populateLists();
        });
        add(resetToDefaultsButton);
    }

    public Tag<?>[] getCurrentlySelectedStateTags() {
        return currentlySelectedStateTags;
    }

    public Tag<?>[] getCurrentlySelectedActionTags() {
        return currentlySelectedActionTags;
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
        
        listModel = webdriverList.getModel();
        selectedIndices = new ArrayList<>();
        for (int i=0; i < listModel.getSize(); i++) {
            if (tagSet.contains(listModel.getElementAt(i))) {
                selectedIndices.add(i);
            }
        }

        webdriverList.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());
        
        // Populate Action Tags
        Set<Tag<?>> tagActionSet = new HashSet<>(Arrays.asList(currentlySelectedActionTags));
        ListModel<Tag<?>> actionListModel = generalActionList.getModel();
        List<Integer> selectedActionIndices = new ArrayList<>();
        for (int i=0; i < actionListModel.getSize(); i++) {
            if (tagActionSet.contains(actionListModel.getElementAt(i))) {
                selectedActionIndices.add(i);
            }
        }

        generalActionList.setSelectedIndices(selectedActionIndices.stream().mapToInt(i -> i).toArray());
    }

}
