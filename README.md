## Java-projekt

Repozitorij za Java projekt pri predmetu Programiranje 2.

Avtorja: Ambrož Pleško in Matija Matanić

## Opis projekta

Projekt je preprosta grafična igra šah, napisana v programskem jeziku Java. Igra uporablja grafični vmesnik z `JFrame` in `JPanel`, šahovnica in figure pa se prikazujejo v oknu programa.

Program omogoča igranje šaha za dva igralca na istem računalniku. Igra prepozna, kateri igralec je na potezi, preverja dovoljene premike figur in zazna konec igre.

## Zagon projekta

Projekt je narejen v Eclipse IDE.

Postopek za zagon:

1. Odprite projekt v Eclipse.
2. Odprite datoteko `Main.java`.
3. Klikni gumb `Run`.
4. Odpre se okno z igro Šah.

## Struktura kode

- `Main.java` zažene program.
- `Okno.java` ustvari glavno okno igre.
- `SahPanel.java` skrbi za risanje šahovnice, prikaz figur, klike z miško in logiko igre.
- `Figures.java` predstavlja posamezno figuro in preverja njene osnovne premike.
- Mapa `res` vsebuje slike šahovskih figur.
