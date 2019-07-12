package ru.DanilovAF.util.xml;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by aleksandr.danilov on 09.04.2019.
 */
public class xNodeTest {

	@Test
	public void test_getNodeMaxLvl() throws Exception {

		myXmlParser xml = new myXmlParser();
		xml.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\PilotSSW\\cmd_NewNumber.xml"));
		xNode n = xml.getLastTree();

		xNode nn = n.getNodeMaxLvl();
		System.out.println(nn.getXML(true));

	}

	@Test
	public void test_cloneFrom() throws Exception {
		myXmlParser xml = new myXmlParser();
		xml.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\PilotSSW\\cmd_NewNumber.xml"));
		xNode n = xml.getLastTree();

		xNode nn = xNode.cloneFrom(n);
		System.out.println(nn.getXML(true));


	}
}