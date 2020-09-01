package gameServer;
/**
 * Salje preko konekcije korisnicima da su povezani i protivnikovo ime radi provere.
 * Takodje salje klientima dali se njihov player nalazi na levoj (L) ili desnij (R) strani ekrana.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class Binder {

	public static void bind(User u1, User u2) {
		
		u1.out.println("L;");
		u2.out.println("R;");
		
		
		u1.out.println("connect;" + u2.username + ";");
		u2.out.println("connect;" + u1.username + ";");

	}

}
