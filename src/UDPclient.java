
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.security.auth.callback.ConfirmationCallback;

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
	
	public Double sendAndReceive(String host, int port, double x, double y, char operazione) throws UnsupportedEncodingException, UnknownHostException, IOException, calcoloErrato
	{
		//byte[] bufferRequest=new byte[8192];
		byte[] bufferAnswer=new byte[13];
		DatagramPacket request;
		DatagramPacket answer;
		ByteBuffer data= ByteBuffer.allocate(18);
		
		InetAddress address=InetAddress.getByName(host);
		double risultato = 0;
		int codiceRisultato = 0;
		
		data.clear();
		data.putDouble(x);
		data.putDouble(y);
		data.putChar(operazione);
		data.flip();
		request=new DatagramPacket(data.array(),data.limit(), address, port);
		answer=new DatagramPacket(bufferAnswer, bufferAnswer.length);
		socket.send(request);
		socket.receive(answer);
		if (answer.getAddress().getHostAddress().compareTo(host)==0 && answer.getPort()==port)
		{
			data.clear();
			data.put(answer.getData());
			data.flip();
			risultato=data.getDouble();
			codiceRisultato=data.getInt();
		}
		closeSocket();
		
		if (codiceRisultato==0)
			return risultato;
		else
			throw new calcoloErrato(codiceRisultato);
		
		
	}
	
	public static void main(String[] args) 
	{
		double rispostaServer;
		String host="127.0.0.1";
		int port=2000;
		
		double x=10;
		double y=0;
		char operazione='+';
		try 
		{
			UDPclient echoClient=new UDPclient();
			rispostaServer=echoClient.sendAndReceive(host, port, x,y,operazione);
			System.out.println("Calcolo effettiuato dal server: "+x+operazione+y+"="+rispostaServer);
		} 
		catch (SocketTimeoutException e)
		{
			System.err.println("Il server non risponde");
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
		catch (calcoloErrato e) 
		{
			System.err.println(e.toString());
		}
		

	}

}
