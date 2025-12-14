// Arquivo Log.java
// Implementa��o das Classes do Grupo Utilit�rio da Biblioteca de Simula��o JAVA
// 01.Nov.1999	Wladimir

package simula;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**

 */
public class Log
{
	private static PrintStream os;
	// aqui uma ref ao obj servidor de mensagens
	
	/**
	 */
	public static boolean OpenFile()
	{
		SimpleDateFormat formatter
     = new SimpleDateFormat ("yyyy,MM,dd-HH'h'mm'm'ss's'");
 		Date currentTime_1 = new Date();
 		String dateString = formatter.format(currentTime_1);
 		try
		{
			FileOutputStream ofile = new FileOutputStream("sim" + dateString + ".log");	
			os = new PrintStream(ofile);
		}
		catch(FileNotFoundException x){return false;}
		
		return true;
	}

	/**
	 * Fecha log.
	 */
	public static synchronized void Close()
	{
		if(os != null)
		{
			os.close();
			os = null;
		}
	}
	
	protected Log()
		{throw new RuntimeException("Classe n�o pode ser instanciada");}
	
	/**
	 * Registra entrada no log
	 */
	public static synchronized void LogMessage(String entry)
	{
		if(os != null)
		{
			os.println(entry);
			os.flush();
		}
	}
}