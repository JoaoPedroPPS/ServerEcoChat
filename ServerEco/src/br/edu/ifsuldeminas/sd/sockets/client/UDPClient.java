package br.edu.ifsuldeminas.sd.sockets.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPClient {
	private static final int TIME_OUT = 500000;
	private static int SERVER_PORT = 3000;
	private static int BUFFER_SIZE = 200;
	private static String KEY_TO_EXIT = "q";

	public static void main(String[] args) {
		DatagramSocket datagramSocket = null;
		Scanner reader = new Scanner(System.in);
		String stringMessage = "";

		try {
			datagramSocket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName("localhost"); // Use localhost for local testing
			while (!stringMessage.equals(KEY_TO_EXIT)) {
				System.out.printf("Escreva uma mensagem (%s para sair): ", KEY_TO_EXIT);
				stringMessage = reader.nextLine();
				if (!stringMessage.equals(KEY_TO_EXIT)) {
					byte[] message = stringMessage.getBytes();
					DatagramPacket datagramPacketToSend = new DatagramPacket(message, message.length, serverAddress,
							SERVER_PORT);
					datagramSocket.send(datagramPacketToSend);

					byte[] responseBuffer = new byte[BUFFER_SIZE];
					DatagramPacket datagramPacketForResponse = new DatagramPacket(responseBuffer,
							responseBuffer.length);
					datagramSocket.setSoTimeout(TIME_OUT);

					try {
						datagramSocket.receive(datagramPacketForResponse);
						String receivedMessage = new String(datagramPacketForResponse.getData(), 0,
								datagramPacketForResponse.getLength());
						System.out.printf("Resposta do servidor: %s\n", receivedMessage);
					} catch (SocketTimeoutException e) {
						System.out.printf("Sem resposta do servidor de eco UDP.\n");
					}
				} else {
					closeOpenedResources(datagramSocket, reader);
					System.out.printf("Cliente saindo com %s ...\n", KEY_TO_EXIT);
				}
			}
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeOpenedResources(datagramSocket, reader);
		}
	}

	private static void closeOpenedResources(DatagramSocket datagramSocket, Scanner reader) {
		if (datagramSocket != null)
			datagramSocket.close();
		if (reader != null)
			reader.close();
	}
}