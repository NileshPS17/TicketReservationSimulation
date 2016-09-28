import java.awt.EventQueue;

import javax.swing.UIManager;

import com.alphaworks.ui.MainWindow;

public class Launcher {
	public static void main(String... args) throws Exception {

		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				new MainWindow().setVisible(true);

			}
		});
	}
}
