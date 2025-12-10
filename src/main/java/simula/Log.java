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
 * Classe que implementa log da simula��o. Pode ser conectada a um
 * arquivo ou a um objeto servidor de mensagens para distribuir as
 * mensagens a diversos usu�rios distribu�dos.
 * No caso de utiliza��o de log em arquivo, este ser� gravado em 
 * arquivo texto de nome "sim"aaaa/mm/dd-hh:mm:ss.log
 */
public class Log
{
	private static PrintStream os;
	// aqui uma ref ao obj servidor de mensagens
	
	/**
	 * Abre arquivo de log para iniciar a sess�o.
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