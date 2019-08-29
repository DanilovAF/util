package ru.DanilovAF.util.http;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aleksandr.danilov on 08.04.2019.
 */
public class HttpQueryTest {

	@Test
	public void test_HttpQuery() throws Exception {
		HttpQuery http = new HttpQuery("172.17.0.226", "napi", "napi", "/openmn/nb/NBProvisioningWebService");
		http.setHeaderForSOAP();

//		String sQ = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:iskratel-si:itnbsp-1-0\"><soapenv:Body><urn:createSubscriber><subscriber node=\"23260\" countryCode=\"7\" areaCode=\"81837\" dn=\"31434\"><param key=\"subscriberType\" value=\"SIP\"/><param key=\"registrationMode\" value=\"registration\"/><param key=\"madName\" value=\"kot-ger1\"/><param key=\"supplSrvSetId\" value=\"1000\"/><param key=\"profileId\" value=\"1\"/><param key=\"registrationExpires\" value=\"3600\"/><param key=\"subscriptionExpires\" value=\"0\"/><param key=\"alias\" value=\"31434\"/><paramList key=\"uri\"/><param key=\"encryptionKey\" value=\"\"/><paramList key=\"authMode\"><param key=\"register\" value=\"yes\"/><param key=\"invite\" value=\"no\"/><param key=\"subscribe\" value=\"no\"/></paramList></subscriber></urn:createSubscriber></soapenv:Body></soapenv:Envelope>";
		String sQ = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:iskratel-si:itnbsp-1-0\">\n" +
				"    <soapenv:Body>\n" +
				"        <urn:createSubscriber>\n" +
				"            <subscriber node=\"23260\" countryCode=\"7\" areaCode=\"81837\" dn=\"31434\">\n" +
				"                <param key=\"subscriberType\" value=\"SIP\"/>\n" +
				"                <param key=\"registrationMode\" value=\"registration\"/>\n" +
				"                <param key=\"madName\" value=\"kot-ger1\"/>\n" +
				"                <param key=\"supplSrvSetId\" value=\"1000\"/>\n" +
				"                <param key=\"profileId\" value=\"1\"/>\n" +
				"                <param key=\"registrationExpires\" value=\"3600\"/>\n" +
				"                <param key=\"subscriptionExpires\" value=\"0\"/>\n" +
				"                <param key=\"alias\" value=\"31434\"/>\n" +
				"                <paramList key=\"uri\"/>\n" +
				"                <param key=\"encryptionKey\" value=\"\"/>\n" +
				"                <paramList key=\"authMode\">\n" +
				"                    <param key=\"register\" value=\"yes\"/>\n" +
				"                    <param key=\"invite\" value=\"no\"/>\n" +
				"                    <param key=\"subscribe\" value=\"no\"/>\n" +
				"                </paramList>\n" +
				"            </subscriber>\n" +
				"        </urn:createSubscriber>\n" +
				"    </soapenv:Body>\n" +
				"</soapenv:Envelope>\n";
		StringBuffer sb = http.execSyn(sQ);
		System.out.println(sb);
	}

	@Test
	public void test_HttpQuery2() throws Exception {
		HttpQuery http = new HttpQuery("10.162.16.16", "/openmn/nb/NBProvisioningWebService");
		http.setHeaderForSOAP();


		//		String sQ = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:iskratel-si:itnbsp-1-0\"><soapenv:Body><urn:createSubscriber><subscriber node=\"23260\" countryCode=\"7\" areaCode=\"81837\" dn=\"31434\"><param key=\"subscriberType\" value=\"SIP\"/><param key=\"registrationMode\" value=\"registration\"/><param key=\"madName\" value=\"kot-ger1\"/><param key=\"supplSrvSetId\" value=\"1000\"/><param key=\"profileId\" value=\"1\"/><param key=\"registrationExpires\" value=\"3600\"/><param key=\"subscriptionExpires\" value=\"0\"/><param key=\"alias\" value=\"31434\"/><paramList key=\"uri\"/><param key=\"encryptionKey\" value=\"\"/><paramList key=\"authMode\"><param key=\"register\" value=\"yes\"/><param key=\"invite\" value=\"no\"/><param key=\"subscribe\" value=\"no\"/></paramList></subscriber></urn:createSubscriber></soapenv:Body></soapenv:Envelope>";
		String sQ = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:iskratel-si:itnbsp-1-0\">\n" +
				"    <soapenv:Body>\n" +
				"        <urn:createSubscriber>\n" +
				"        </urn:createSubscriber>\n" +
				"    </soapenv:Body>\n" +
				"</soapenv:Envelope>\n";
		boolean bb = true;

		while(bb) {
			StringBuffer sb = http.execSyn(sQ);
			System.out.println(sb);
			bb = false;
		}
	}

}
