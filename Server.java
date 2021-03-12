import java.awt.BorderLayout;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Server {

	public static void main(String[] args) {

		MarcoServidor mimarco = new MarcoServidor();

		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

class MarcoServidor extends JFrame implements Runnable {

	public MarcoServidor() {

		setBounds(1200, 300, 280, 350);

		JPanel milamina = new JPanel();

		milamina.setLayout(new BorderLayout());

		areatexto = new JTextArea();

		milamina.add(areatexto, BorderLayout.CENTER);

		add(milamina);

		setVisible(true);

		Thread mihilo = new Thread(this);

		mihilo.start();

	}

	@Override
	public void run() {

		try {

			ServerSocket servidor = new ServerSocket(2222);

			String nick, ip, mensaje;
			
			ArrayList <String> listaIp = new ArrayList<String>();

			PaqueteEnvio paquete_recibido;

			// hace que el servidor este siempre a la escucha
			while (true) {

				// permite todas las conexiones
				Socket misocket = servidor.accept();

				ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());

				paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();

				nick = paquete_recibido.getNick();

				ip = paquete_recibido.getIp();

				mensaje = paquete_recibido.getMensaje();
				
				

				if (!mensaje.equals(" online")) {

					areatexto.append("\n" + paquete_recibido.getNick() + ": " + mensaje + " para " + ip);

					Socket enviaDestinatario = new Socket(ip, 9090);

					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());

					paqueteReenvio.writeObject(paquete_recibido);

					paquete_datos.close();

					paqueteReenvio.close();

					enviaDestinatario.close();

					misocket.close();

				} else {

					// -------------------------Dectectar Usuarios Online ----------------------------------------
					 

					InetAddress localizacion = misocket.getInetAddress();

					// cambia la direcion ip recibida antes y la transforma en un string

					String IpRemota = localizacion.getHostAddress();
					
					
					listaIp.add(IpRemota);
					
					
					paquete_recibido.setIps(listaIp);
					

					for(String z: listaIp) {
						
						
						System.out.println("Array" +z);
						
						
						Socket enviaDestinatario = new Socket(z, 9090);

						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());

						paqueteReenvio.writeObject(paquete_recibido);

						paquete_datos.close();

						paqueteReenvio.close();

						enviaDestinatario.close();

						misocket.close();
						
						
						
					}

					// --------------------------------------------------------------------------------------------

				}

			}

		} catch (IOException ex) {
			Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private JTextArea areatexto;

}
