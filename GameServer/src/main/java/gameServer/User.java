package gameServer;

import java.io.PrintWriter;
/**
 * Kalsa koja predstavlja korisnika. sadrzi njegovo korisnicko ime. Protivnika sa kojim se povezuje. 
 * i printwriter uzpomoc kog se preko konekcije korisniku salje poruka.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class User {

	public String username;
	public String oponent;
	public PrintWriter out;

	public User(String username, String oponent, PrintWriter out) {
		this.username = username;
		this.oponent = oponent;
		this.out = out;
	}

}
