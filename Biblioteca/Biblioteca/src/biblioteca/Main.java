package biblioteca;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        LibraryRepository libraryRepo = new LibraryRepositoryXML("src\\biblioteca\\libri.xml");
        LibraryService libraryService = new LibraryService(libraryRepo);
        UserRepository userRepo = new UserRepositoryXML("src\\biblioteca\\utenti.xml");
        UserService userService = new UserService(userRepo);
        Scanner scanner = new Scanner(System.in);
        boolean authenticated = false;
        User user = null;

        while (!authenticated) {

            System.out.println("Benvenuto! Accedi se hai gia' un account o registrati per crearne uno nuovo!");
            System.out.println("###################");
            System.out.println("   1. Accedi");
            System.out.println("   2. Registrarti");
            System.out.println("   0. Exit");
            System.out.println("###################");
            System.out.print("\nScelta: ");
            String choice = scanner.nextLine();

            switch (choice.replaceAll("\\s+", "")) {
                case "1" -> {
                    System.out.print("Inserisci il tuo username: ");
                    String name = scanner.nextLine().replaceAll("\\s+", "");
                    System.out.print("Inserisci la password: ");
                    String password = scanner.nextLine().replaceAll("\\s+", "");

                    try {
                        user = userService.authUser(name, password);
                        authenticated = true;
                        System.out.println("Accesso effettuato!\n");
                    } catch (Exception ex) {
                        System.out.println("Username o password errati\n");
                    }
                }
                case "2" -> {
                    System.out.print("Inserisci il tuo username: ");
                    String username = scanner.nextLine().replaceAll("\\s+", "");
                    System.out.print("Inserisci la password: ");
                    String password = scanner.nextLine().replaceAll("\\s+", "");
                    try {
                        userService.addUser(username, password);
                        System.out.println("Registrazione effettuata!\n");
                    } catch (Exception ex) {
                        System.out.println("Nome utente gia' in uso, trova un username unico!\n");
                    }
                }
                case "0" -> {
                    System.out.println("Arrivederci!");
                    return;
                }
                default ->
                    System.out.println("Comando non riconosciuto, controlla di aver digitato correttamente!");
            }
        }

        System.out.println("Ciao " + user.getName() + ", benvenuto nella biblioteca, cosa vuoi fare?");
        String choice;
        do {
            System.out.println("########################################");
            System.out.println("    1. Aggiungere libri al catalogo");
            System.out.println("    2. Eliminare libri dal catalogo");
            System.out.println("    3. Prendi in prestito un libro");
            System.out.println("    4. Restituire un libro");
            System.out.println("    5. Visualizza libri disponibili");
            System.out.println("    6. Cerca libro");
            System.out.println("    7. Situazione prestiti");
            System.out.println("    8. Storico");
            System.out.println("    0. Esci");
            System.out.println("########################################");

            System.out.print("\nScelta: ");
            choice = scanner.nextLine();

            switch (choice.replaceAll("\\s+", "")) {
                case "1" -> {
                    System.out.print("Quanti libri desideri aggiungere? ");
                    int n = scanner.nextInt();
                    scanner.nextLine();

                    for (int i = 0; i < n; i++) {
                        System.out.print("Inserisci il titolo del libro che vuoi aggiungere: ");
                        String title = scanner.nextLine();

                        // Verifica se il libro esiste già nell'archivio
                        if (libraryService.hasBook(title)) {
                            // Se il libro esiste già, aumenta semplicemente la quantità disponibile
                            System.out.print("Il libro è gia' presente in archivio, inserisci il numero di copie da aggiungere: ");
                            int quantity = scanner.nextInt();
                            try {
                                Book book = libraryService.getBook(title);
                                libraryService.addOrUpdateBook(book.getTitle(), book.getAuthor(), book.getPublisher(), book.getGenre(), book.getYear(), quantity);
                            } catch (Exception ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            scanner.nextLine();
                        } else {
                            // Se il libro non esiste, aggiungilo all'archivio
                            System.out.print("Inserisci l'autore del libro da aggiungere: ");
                            String author = scanner.nextLine();
                            System.out.print("Inserisci la casa editrice del libro da aggiungere: ");
                            String publisher = scanner.nextLine();
                            System.out.print("Inserisci il genere del libro da aggiungere: ");
                            String genre = scanner.nextLine();
                            System.out.print("Inserisci l'anno di pubblicazione del libro da aggiungere: ");
                            int year = scanner.nextInt();
                            System.out.print("Inserisci il numero di copie del libro da aggiungere: ");
                            int quantity = scanner.nextInt();
                            libraryService.addOrUpdateBook(title, author, publisher, genre, year, quantity);
                            scanner.nextLine();
                        }
                    }
                    System.out.println("Catalogo aggiornato!\n");
                    break;
                }

                case "2" -> {
                    System.out.print("Quanti libri vuoi eliminare? ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    for (int i = 0; i < n; i++) {
                        System.out.print("Inserisci il titolo del libro che desideri eliminare: ");
                        String title = scanner.nextLine();
                        if (libraryService.hasBook(title)) {
                            System.out.print("Inserisci il numero di copie che desideri eliminare (" + libraryService.getBookQuantity(title) + " disponibili): ");
                            int quantity = scanner.nextInt();
                            libraryService.removeQuantities(title, quantity);
                            System.out.println("Catalogo aggiornato!\n");
                            scanner.nextLine();
                        } else {
                            System.out.println("Libro non presente in archivio, controlla di aver digitato correttamente!\n");
                            break;
                        }
                    }
                }

                case "3" -> {
                    System.out.print("Quanti libri vuoi prendere in prestito? ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    if (n > libraryService.countBooks()) {
                        System.out.println("Hai selezionato un numero di libri maggiore rispetto a quelli presenti nel catalogo");
                        break;
                    }
                    for (int i = 0; i < n; i++) {
                        System.out.print("Inserisci il titolo del libro che desideri prendere in prestito: ");
                        String title = scanner.nextLine();

                        // Ottieni la quantità disponibile di questo libro dal documento XML
                        int currentQuantity = libraryService.getBookQuantity(title);
                        if (currentQuantity == 0) {
                            System.out.println("Spiacenti, il libro non è attualmente disponibile\n");
                            break; // Interrompi il ciclo se il libro non è disponibile
                        }

                        // Stampa la quantità disponibile nel messaggio per l'utente
                        System.out.print("Quante copie vuoi prendere in prestito? (" + currentQuantity + " disponibili): ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();

                        try {
                            libraryService.borrow(title, quantity);
                            userService.addBook(user, title, quantity);
                            System.out.println("Hai preso il libro in prestito!\n");
                        } catch (Exception ex) {
                            ex.printStackTrace(new java.io.PrintStream(System.out));
                            System.out.println("Il numero di copie inserita è maggiore di quelle disponibili\n");
                        }
                    }
                }

                case "4" -> {
                    System.out.print("Quanti libri vuoi restituire? ");
                    int n = scanner.nextInt();
                    scanner.nextLine();

                    for (int i = 0; i < n; i++) {
                        System.out.print("Inserisci il titolo del libro che desideri restituire: ");
                        String title = scanner.nextLine();

                        // Consentire all'utente di restituire fino al limite massimo di libri in possesso
                        int userBookQuantity = userService.getBookQuantity(user, title);
                        // Se l'utente non ha il libro
                        if (userBookQuantity == 0) {
                            System.out.println("Non hai in prestito il libro!\n");
                            break;
                        }
                        System.out.print("Quante copie vuoi restituire? (" + userBookQuantity + " disponibili): ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();

                        // Restituire solo fino al limite massimo di copie in possesso
                        if (quantity > userBookQuantity) {
                            System.out.println("Hai selezionato un numero di copie maggiore rispetto a quelle in tuo possesso\n");
                            break;
                        }
                        try {
                            userService.returnBook(user, title, quantity);
                            libraryService.updateQuantity(title, quantity);
                            System.out.println("Restituzione effettuata!\n");
                        } catch (Exception ex) {
                            System.out.println("Errore\n");
                        }
                    }
                }

                case "5" -> {
                    try {
                        System.out.println("I libri presenti nella biblioteca sono i seguenti: ");
                        System.out.println(libraryService.getBookTitlesAsString());
                    } catch (Exception ex) {
                        System.out.println("Non ci sono libri disponibili nella biblioteca attualmente\n");
                    }
                }

                case "6" -> {
                    System.out.print("Che libro desideri cercare: ");
                    String title = scanner.nextLine();
                    try {
                        System.out.println("Ecco i dettagli del libro richiesto: ");
                        libraryService.searchBook(title);
                    } catch (Exception ex) {
                        System.out.println("Libro non trovato \n");
                    }
                }
                case "7" -> {
                    try {
                        String bookTitles = userService.getBookTitlesAsString(user);
                        if (bookTitles.isEmpty()) {
                            System.out.println("Attualmente non hai alcun libro in prestito\n");
                        } else {
                            System.out.println("I libri che hai attualmente in prestito sono i seguenti: ");
                            System.out.println(bookTitles);
                        }
                    } catch (Exception ex) {
                        // Gestione di un eventuale eccezione
                        System.out.println("Si è verificato un errore nel recuperare i libri in prestito\n");
                    }
                }

                case "8" -> {
                    List<History> history = userService.getHistory(user);
                    if (history.isEmpty()) {
                        System.out.println("L'utente non ha mai svolto azioni");
                    } else {
                        System.out.println("Ecco lo storico delle azioni:");
                        for (int i = 0; i < history.size(); i++) {
                            System.out.println("Tipo di azione: " + history.get(i).getUserAction() + ", Titolo: " + history.get(i).getTitle() + ", numero di copie: " + history.get(i).getQuantity() + ", in data: " + history.get(i).getDate());
                        }
                    }
                    System.out.print("\n");
                }

                case "0" ->
                    System.out.println("Arrivederci!");

                default ->
                    System.out.println("Comando non riconosciuto\n");
            }

        } while (!choice.equals("0"));
    }
}
