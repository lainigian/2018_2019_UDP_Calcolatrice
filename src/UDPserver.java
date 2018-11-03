// Protocollo:i dati trasmessi dal client sono x:doube, y:double, operazione:char (+,-,*,/,^)
// Suggerimento: per verificare la correttezza del calcolo, nella risposta 
// si potrebbe inviare, oltre al risultato, anche un codice (un char): 0=ok, 1=Errore
// il controllo sui dati di input va svolto nel client per evitare di occupare 
// banda con richieste contenente dati non corretti.
// Porta 1100

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
		byte[] bufferRequest= new byte[20]; //double, double, byte: tot 9 byte
		byte[] bufferAnswer= new byte[8]; //double
		DatagramPacket request=new DatagramPacket(bufferRequest, bufferRequest.length);
		DatagramPacket answer;
		double x,y;
		char operazione;
		double risultato = 0;
		ByteBuffer data=ByteBuffer.allocate(20);
		
		boolean error=false; //eventuale errore nel dato ricevuto
		while (!interrupted())
		{
			try 
			{
				socket.receive(request);
				//estraggo i dati dal datagramma e li metto nel bytebuffer data
				data.clear();
				data.put(request.getData());
				//estraggo i dati dal bytebuffer data
				data.flip();
				x=data.getDouble();
				y=data.getDouble();
				operazione=data.getChar();
				
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
					risultato=x/y;
					break;
					
				case '^':
					risultato=Math.pow(x, y);
					break;
					
				default:
					break;
				}
				
				
				data.clear();
				data.putDouble(risultato);
				data.flip(); //attenzione: prima di leggere è necessario flip()!
				answer= new DatagramPacket(data.array(),data.array().length, request.getAddress(),request.getPort());
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
			UDPserver echoServer= new UDPserver(1100);
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

}
