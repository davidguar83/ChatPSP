import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class Cliente {

	public static void main(String[] args) {

		MarcoCliente mimarco = new MarcoCliente();

		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

class MarcoCliente extends JFrame {

	public MarcoCliente() {

		setBounds(600, 300, 280, 350);

		LaminaMarcoCliente milamina = new LaminaMarcoCliente();

		add(milamina);

		setVisible(true);

		addWindowListener(new EnviarConexiones());

	}

}

class EnviarConexiones extends WindowAdapter {

	public void windowOpened(WindowEvent e) {
		try {

			Socket misocket = new Socket("192.168.1.37", 2222);

			PaqueteEnvio datos = new PaqueteEnvio();

			datos.setMensaje(" online");

			ObjectOutputStream paquete_datos;

			paquete_datos = new ObjectOutputStream(misocket.getOutputStream());

			paquete_datos.writeObject(datos);

			misocket.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}

class LaminaMarcoCliente extends JPanel implements Runnable {

	private JComboBox ip;

	private JLabel nick;

	private JTextField campo1;

	private JButton miboton;

	private JTextArea campchat;

	public LaminaMarcoCliente() {

		String nick_usuario = JOptionPane.showInputDialog("Nick: ");

		JLabel n_nick = new JLabel("Nick: ");

		add(n_nick);

		nick = new JLabel();

		nick.setText(nick_usuario);

		add(nick);

		JLabel texto = new JLabel("Conectar: ");

		add(texto);

		ip = new JComboBox();

		add(ip);

		campo1 = new JTextField(20);

		add(campo1);

		campochat = new JTextArea(12, 20);

		add(campochat);

		miboton = new JButton("Enviar");

		EnviarTexto mievento = new EnviarTexto();

		miboton.addActionListener(mievento);

		add(miboton);

		Thread mihilo = new Thread(this);

		mihilo.start();

	}

	public class EnviarTexto implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			campchat.append("\nyo: " + campo1.getText());

			try {
				Socket misocket = new Socket("192.168.1.37", 2222);

				PaqueteEnvio datos = new PaqueteEnvio();

				datos.setNick(nick.getText());
				datos.setIp(ip.getSelectedItem().toString());
				datos.setMensaje(campo1.getText());

				ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());

				paquete_datos.writeObject(datos);

				campo1.setText("");

				paquete_datos.close();

				misocket.close();

				/*
				 * DataOutputStream salida= new DataOutputStream(misocket.getOutputStream());
				 * 
				 * 
				 * salida.writeUTF(campo1.getText());
				 * 
				 * 
				 * salida.close();
				 * 
				 */

			} catch (UnknownHostException ex) {

				ex.printStackTrace();

			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}

		}

	}

	private JTextArea campochat;

	@Override
	public void run() {

		try {

			ServerSocket servidor_cliente = new ServerSocket(9090);

			Socket cliente;

			PaqueteEnvio paqueteRecibido;

			while (true) {

				cliente = servidor_cliente.accept();

				ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());

				paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject();

				if (!paqueteRecibido.getMensaje().equals(" online")) {

					campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());

				} else {

					ArrayList<String> menuComboBoxIPs = new ArrayList<String>();

					menuComboBoxIPs = paqueteRecibido.getIps();

					ip.removeAllItems();

					for (String a : menuComboBoxIPs) {

						ip.addItem(a);

					}
					
					// campochat.append("\n " + paqueteRecibido.getIps());
					
					
					flujoentrada.close();

					cliente.close();

				}

				

			}

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

	}

}

class PaqueteEnvio implements Serializable {

	private String nick, mensaje, ip;

	private ArrayList<String> Ips;

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
