package sha1;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import sha1.OmerHSM.Client;

public class Run {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Hash'i alinacak veri yok!");
			return;
		}
		else if (args.length > 1) {
			System.out.println("En fazla bir verinin hash'ini alabilirsiniz!");
			return;
		}
		
		
		
		for (String string : args) {
			System.out.println("Argumanlar : " + string);
		}
		String ipAddress = "192.168.1.40";
		Integer port = 1453;
		TTransport transport;
		transport = new TSocket(ipAddress, port);
		try {
			transport.open();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		TProtocol protocol = new TBinaryProtocol(transport);
		OmerHSM.Client client = new OmerHSM.Client(protocol);
		perform(client, args[0]);
	}

	private static void perform(Client client, String sendStringMessage) {
		String digest = null;
		String result = null;
		try {
			Integer returnValue = client.ping();
			result = client.sha1(sendStringMessage,sendStringMessage.length() , digest);
		} catch (TException e) {
			e.printStackTrace();
		}
		
		System.out.println("sha 1 = "+ result);
		
	}

}
