/**
 * Protocollo: 
 * request --> double x, double y, char operazione
 * answer --> double risultato, int codiceRisultato 
 * 0-->ok
 * -1-->divisone per zero
 * -2-->operazione non prevista)
 * 
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class UDPserver extends Thread
{

	private DatagramSocket socket;
	
	public UDPserver (int port) throws SocketException
	{
		socket=new DatagramSocket(port);
		socket.setSoTimeout(1000);
		
	}
	
	public void run()
	{
		byte[] bufferRequest= new byte[18]; //due double e un char
		byte[] bufferAnswer= new byte[13]; 	//due double una stringa con: ok, oppure div0 oppure error (simbolo operazione non valido)
		
		DatagramPacket request=new DatagramPacket(bufferRequest, bufferRequest.length);
		DatagramPacket answer;
		ByteBuffer data= ByteBuffer.allocate(18);
		char operazione;
		double x;
		double y;
		double risultato = 0;
		int codiceRisultato = 0; //0,-1,-2
		
		while (!interrupted())
		{
			try 
			{
				socket.receive(request);
				
				data.clear();
				data.put(request.getData());
				data.flip();
				x=data.getDouble();
				y=data.getDouble();
				operazione=data.getChar();
				
				try 
				{
					risultato=calcola( x,  y,  operazione);
					codiceRisultato=0;
				} 
				catch (zeroDivision e) 
				{
					codiceRisultato=-1;
				} 
				catch (wrongOperation e) 
				{
					codiceRisultato=-2;
				}
				
				data.clear();
				data.putDouble(risultato);
				data.putInt(codiceRisultato);
				data.flip();
				answer=new DatagramPacket(data.array(), data.limit(), request.getAddress(), request.getPort());
				socket.send(answer);		
			} 
			catch (SocketTimeoutException e) 
			{
				System.err.println("Timeout");
			}
			catch (IOException e) 
			{
				
				e.printStackTrace();
			} 
			
		}
		closeSocket();
		
	}
	
	public void closeSocket()
	{
		socket.close();
	}
	public static void main(String[] args)
	{
		ConsoleInput tastiera= new ConsoleInput();
		try 
		{
			UDPserver echoServer= new UDPserver(2000);
			echoServer.start();
			tastiera.readLine();
			echoServer.interrupt();
			
		} 
		catch (SocketException e) 
		{
			System.err.println("Impossibile istanziare il socket");
		} 
		catch (IOException e) 
		{
			System.out.println("Errore generico di I/O dalla tastiera");
		}

	}
	
	private double calcola(double x, double y, char operazione) throws zeroDivision, wrongOperation
	
	{
		double risultato;
		switch (operazione) 
		{
		case '+':
			risultato=x+y;
			break;
		case '-':
			risultato=x-y;
			break;
		case '*':
			risultato=x*y;
			break;
		case '/':
			if (y==0)
			{
				throw new zeroDivision();
			}
			risultato=x/y;
			break;
		case '^':
			risultato=Math.pow(x, y);
			break;
		default:
			throw new wrongOperation();
		}
		return risultato;
	}

}
