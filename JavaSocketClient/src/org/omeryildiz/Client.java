package org.omeryildiz;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private static Socket socket;

	public static void main(String[] args) {
		try {
			socket = new Socket("192.168.1.40",5000);
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			bos.write("Goodbye Dennis Ritche".getBytes());
			bos.flush();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
