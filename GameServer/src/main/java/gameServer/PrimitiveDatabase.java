package gameServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
/**
 * Primitivna baza podataka koja direktno upisuje i izcitava korisnike u zadati fajl.
 * Usernme od svakog korisnika predstavlja kljuc u bazi.
 * <br>
 * createdby: Filip Hadzi-Ristic & Stefan Ginic
 * 
 * @version 1.0
 *
 */
public class PrimitiveDatabase {

	FileWriter writer;
	Scanner scanner;
	File file;
	String str;

	public PrimitiveDatabase(String filePath) throws Exception {
		file = new File(filePath);
		writer = new FileWriter(file, true);
		scanner = new Scanner(file);
		writer.close();
		scanner.close();
	}

	/**
	 * Dodaje korisnika u bazu
	 * @param key usernam eo dkorisnika
	 * @param value sifra od korisnika
	 */
	public void put(String key, String value) {
		try {
			writer = new FileWriter(file, true);
			writer.write(key + ";" + value + "\n");
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Preuzimanje korisnika iz baze.
	 * @param key username od korisnika
	 * @return vraca sifru od datog korisnika ako postoji iz baze.
	 */
	public String get(String key) {
		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				str = scanner.nextLine();
				if (str.split(";")[0].equals(key)) {
					scanner.close();
					return str.split(";")[1];
				}

			}
			scanner.close();

		} catch (Exception e) {
			e.printStackTrace();
			scanner.close();
		}

		return "";
	}

	/**
	 * Proverava dali postoji korisnik sa datim username-om u bazi
	 * @param key username od korisnika
	 * @return vraca true ili false u zavisnosti od toga dali postoji ili ne.
	 */
	public boolean containsKey(String key) {
		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				str = scanner.nextLine();
				if (str.split(";")[0].equals(key)) {
					scanner.close();
					return true;
				}

			}
			scanner.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			scanner.close();
		}

		return false;
	}

}
