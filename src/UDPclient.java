
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class UDPclient 
{

	private DatagramSocket socket;
	
	public UDPclient() throws SocketException
	{
		socket= new DatagramSocket();
		socket.setSoTimeout(1000);
	}
	
	public void closeSocket()
	{
		socket.close();
	}
	
	public double sendAndReceive(String host, int port, double x, double y, char operazione) throws UnknownHostException, IOException
	{
		byte[] bufferRequest=new byte[20];
		byte[] bufferAnswer=new byte[8];
		DatagramPacket request;
		DatagramPacket answer;
		ByteBuffer data = ByteBuffer.allocate(20);
		
		InetAddress address=InetAddress.getByName(host);
		double rispostaServer = 0;
		
		//costruisco il bytebuffer per la richiesta
		data.clear();
		data.putDouble(x);
		data.putDouble(y);
		data.putChar(operazione);
		//estraggo l'array di byte dal bytebuffer e lo incapsulo nel datagramma
		data.flip();
		request=new DatagramPacket(data.array(), data.array().length, address, port);
		answer=new DatagramPacket(bufferAnswer, bufferAnswer.length);
		socket.send(request);
		socket.receive(answer);
		if (answer.getAddress().getHostAddress().compareTo(host)==0 && answer.getPort()==port)
		{
			//estraggo l'array di byte dal datagramma answer e lo incapsulo nel bytebuffer
			data.clear();
			data.put(answer.getData());
		}
		closeSocket();
		//estraggo il dato dal bytebuffer
		data.flip();
		rispostaServer=data.getDouble();
		return rispostaServer;
	}
	
	public static void main(String[] args) 
	{
		double rispostaServer;
		String host="127.0.0.1";
		int port=1100;
		double x=10;
		double y=5;
		char operazione='?';

		try 
		{
			UDPclient echoClient=new UDPclient();
			rispostaServer=echoClient.sendAndReceive(host, port,x,y,operazione);
			System.out.println("risultato: \n "+x+ operazione+y+"= " +rispostaServer);
		} 
		catch (SocketTimeoutException e)
		{
			System.err.println("Timeout");
		}
		catch (SocketException e)
		{
			System.err.println("Impossibile istanziare il socket");
		} 
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Charset non supportato");
		} 
		catch (UnknownHostException e) 
		{
			System.err.println("Host sconosciuto");
		} 
		catch (IOException e) 
		{
			System.err.println("Errore generico di I/O");
		}
		

	}

}
