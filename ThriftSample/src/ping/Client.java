package ping;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class Client {

	public static void main(String[] args) {
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
		Invoke.Client client = new Invoke.Client(protocol);
		perform(client);

	}

	private static void perform(ping.Invoke.Client client) {
		try {
			client.ping();
		} catch (TException e) {
			e.printStackTrace();
		}

	}

}
