package smarthand.ui_explorer.strategy.swifthand;

import smarthand.ui_explorer.util.Util;
import smarthand.ui_explorer.visualize.ForwardLabeledGraphPrintTrait;
import smarthand.ui_explorer.visualize.PrinterHelper;
import smarthand.ui_explorer.visualize.Transition;

import java.util.*;

/**
 * Created by wtchoi on 6/22/16.
 */
public class MBRTModelTraversalHelper implements ForwardLabeledGraphPrintTrait<Model.ModelState> {

    MBRTStrategy strategy;

    MBRTModelTraversalHelper(MBRTStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Iterable<Model.ModelState> getRoots() {
        return strategy.model.roots.values();
    }

    @Override
    public Iterable<Model.ModelState> getSuccessors(Model.ModelState node) {
        LinkedList<Model.ModelState> succs = new LinkedList<>();
        for (int i = 0; i<node.abstractUi.getEventCount(); i++) {
            HashSet<Model.ModelState> children = strategy.getChildren(node, i, 0);
            if (children == null) continue;
            for (Model.ModelState succ: children) {
                succs.push(succ);
            }
        }
        return succs;
    }

    @Override
    public Iterable<Transition<Model.ModelState>> getTransitions(Model.ModelState node) {
        LinkedList<Transition<Model.ModelState>> transitions = new LinkedList<>();
        for (int i = 0; i<node.abstractUi.getEventCount(); i++) {
            HashSet<Model.ModelState> children = strategy.getChildren(node, i, 0);
            if (children == null) continue;
            for (Model.ModelState succ: children) {
                transitions.push(new Transition<Model.ModelState>(succ,Integer.toString(i)));
            }
        }
        return transitions;
    }

    @Override
    public int countTransition(Model.ModelState node) {
        int count = 0;
        for (int i = 0; i < node.outTransitions.length; i++) {
            if (node.outTransitions[i] == null) continue;
            count += node.outTransitions[i].size();
        }
        return count;
    }

    @Override
    public int countOutgoingLabels(Model.ModelState node) {
        return node.outTransitions.length;
    }

    @Override
    public String getNodeName(Model.ModelState node) {
        return node.abstractUi.id() + ":" + node.id + "[" + node.abstractUi.getEventCount() + "]";
    }

    @Override
    public String getNodeDetail(Model.ModelState node) {
        return node.abstractUi.getTooltip();
    }

    @Override
    public boolean isColoredNode(Model.ModelState node) {
        if (node == strategy.current) return true;
        if (node == strategy.prev) return true;
        return false;
    }

    @Override
    public boolean isBoldNode(Model.ModelState node) {
        if (node == strategy.prev) return true;
        if (node == strategy.current) return true;
        return false;
    }

    @Override
    public String getNodeColor(Model.ModelState node) {
        if (node == strategy.current) return "red";
        if (node == strategy.prev) return "blue";
        return "black";
    }

    @Override
    public boolean isColoredTransitionGroup(Model.ModelState from, Model.ModelState to, int groupID) {
        return false;
    }

    @Override
    public boolean isBoldTransitionGroup(Model.ModelState from, Model.ModelState to, int groupID) {
        return false;
    }

    @Override
    public boolean isDottedTransitionGroup(Model.ModelState f, Model.ModelState t, int groupID) {
        return false;
    }

    @Override
    public String getTransitionGroupColor(Model.ModelState from, Model.ModelState to, int groupID) {
        return null;
    }

    @Override
    public int getTransitionGroupCount() {
        return 1;
    }

    @Override
    public int getTransitionGroupID(Model.ModelState from, Model.ModelState to, String label) {
        return 0;
    }

    private boolean isPrevAction(Model.ModelState from, Model.ModelState to, String label) {
        return (from == strategy.prev) && (to == strategy.current) && label.equals(String.valueOf(strategy.lastTid));
    }


    @Override
    public boolean isImportantTransition(Model.ModelState from, Model.ModelState to, String label) {
        return isPrevAction(from, to, label);
    }

    @Override
    public boolean isColoredTransition(Model.ModelState from, Model.ModelState to, String label) {
        return isPrevAction(from, to, label);
    }

    @Override
    public boolean isBoldTransition(Model.ModelState from, Model.ModelState to, String label) {
        return isPrevAction(from, to, label);
    }

    @Override
    public boolean isDottedTransition(Model.ModelState from, Model.ModelState to, String label) {
        return false;
    }

    @Override
    public String getTransitionColor(Model.ModelState from, Model.ModelState to, String label) {
        if (from == strategy.prev && to  == strategy.current) return "blue";
        return "black";
    }

    @Override
    public boolean hasTransitionTooltip(Model.ModelState from, Model.ModelState to, String label) {
        return from.mayMethods[Integer.parseInt(label)] != null;
    }

    @Override
    public String getTransitionTooltip(Model.ModelState from, Model.ModelState to, String label) {
        HashSet<Integer> mayMethod = from.mayMethods[Integer.parseInt(label)];
        if (mayMethod == null) return null;

        StringBuilder builder = new StringBuilder();
        Util.makeIntSetToString(mayMethod, ", ", builder);
        return builder.toString();
    }


    @Override
    public boolean hasImage(Model.ModelState node) {
        if (node.isFailState()) return false;
        return true;
    }

    @Override
    public String getImageURI(Model.ModelState node) {
        return "./screen" + node.abstractUi.getSnapshotID() + ".png";
    }

    @Override
    public String compactLabels(Set<String> labels) {
        SortedSet<Integer> sortedLabels = new TreeSet();
        for (String label : labels) {
            sortedLabels.add(Integer.valueOf(label));
        }
        return PrinterHelper.buildIntervalString(sortedLabels);
    }

    @Override
    public boolean hasURL(Model.ModelState node) {
        return false;
        //TODO
    }

    @Override
    public String getURL(Model.ModelState node) {
        return null;
        //TODO
    }

    @Override
    public boolean isGrouped() {
        return false;
        //NOT IMPLEMENTED
    }

    @Override
    public String getGroupID(Model.ModelState node) {
        return null;
        //NOT IMPLEMENTED
    }

    @Override
    public boolean skipNode(Model.ModelState node ){
        return false;
    }
}