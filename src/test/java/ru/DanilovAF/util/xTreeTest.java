package ru.DanilovAF.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.*;

/**
 * Created by aleksandr.danilov on 29.10.2018.
 */
public class xTreeTest {
    private xTree<String> testTree;
    private xTree<String> midleNode;
    private xTree<String> lastNode;

    @Before
    public void setUp() throws Exception {
        testTree = new xTree<String>("Root");
        testTree.addNode(new xTree<String>("Ch1"));
        testTree.addNode(new xTree<String>("Ch2").addNode(new xTree<String>("Ch2.1")).addNode(new xTree<String>("Ch2.2")));
        testTree.addNode(new xTree<String>("Ch3"));
        midleNode = new xTree<String>("Ch4");
        midleNode.addNode(new xTree<String>("Ch4.1"));
        midleNode.addNode(new xTree<String>("Ch4.2"));
        lastNode = new xTree<String>("Ch4.3");
        midleNode.addNode(lastNode);
        testTree.addNode(midleNode);
    }

    @Test
    public void test_getCountAllChildren_metod() throws Exception {
        int y = testTree.getCountAllChildren(0);
        assertEquals(y, 10);
    }

    @Test
    public void test_getRotest_metod() throws Exception {
        xTree<String> buf = lastNode.getRootest();
        assertSame(buf, testTree);
    }

    @Test
    public void test_getCurentLavel_metod() throws Exception {
        int y = lastNode.getCurentLavel();
        assertEquals(y, 3);
    }

    @Test
    public void test_getMaxLavel_metod() throws Exception {
        int y = testTree.getMaxLavel();
        assertEquals(y, 3);
        y = lastNode.getMaxLavel();
        assertEquals(y, 1);
    }

    @Test
    public void test_getAllNodeInList_metod() throws Exception {
        // Заполним дерево и получим все его узлы в виде списка
        ArrayList<xTree<String>> listNodes = testTree.getAllNodeInList();
        assertThat(listNodes).hasSize(10);
    }

    @Test
    public void test_toString_metod() throws Exception {
        String sVal = testTree.toString();
        System.out.println(sVal);
        assertThat(sVal).isNotEmpty().contains("Root\nCh1\nCh2\nCh3\nCh4\nCh2.1\nCh2.2\nCh4.1\nCh4.2\nCh4.3");
    }
}