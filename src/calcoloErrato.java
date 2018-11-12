
public class calcoloErrato extends Exception 
{
	private String errore;
	
	public calcoloErrato(int codiceErrore)
	{
		if (codiceErrore==-1)
			errore="Divisione per zero";
		else
			errore="Tipo operazione non prevista";
	}
	
	public String toString()
	{
		return this.errore;
	}
}
