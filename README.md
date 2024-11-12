<h1>SolarSon</h1>
SolarSon je aplikacija temeljena na Javi, dizajnirana za praćenje solarnih pretvarača i
potrošene električne energije. Pruža podatke u stvarnom vremenu o proizvodnji i potrošnji
solarne energije, omogućujući korisnicima optimizaciju potrošnje energije i automatizaciju
pametnih uređaja.

## Preduvjeti
- Java 8 ili novija verzija
- Maven za upravljanje gradnjom
- Internetska veza za dohvaćanje vremenskih prognoza i drugih vanjskih izvora podataka
- USB kompatibilan pretvarač koji koristi PI30 komunikacijski protokol

## Značajke
- **Praćenje:** Praćenje proizvodnje solarne energije i potrošnje električne energije u stvarnom vremenu.
- **Automatizacija pametnog doma:** Automatizacija pametnih prekidača na temelju vremenske prognoze i podataka o solarnoj energiji.
- **Korisničko sučelje:** Intuitivno dizajnirano za jednostavno korištenje i praćenje.
- **Vizualizacija podataka:** Grafovi i dijagrami za vizualizaciju proizvodnje i potrošnjeenergije

<h1>Aplikacija</h1>
<h2>Početni zaslon</h2>
Podaci o električnoj energiji u stvarnom vremen

![MainPage](https://github.com/user-attachments/assets/b5e5450f-8215-4567-9473-96c290271158)

Komunikacija između računala i invertera omogućena je putem USB kabela u kombinaciji s PI30 komunikacijskim protokolom. USB kabel uspostavlja fizičku vezu između računala i invertera, dok PI30 protokol osigurava pravilnu i učinkovitu razmjenu podataka između ova dva uređaja. Ovaj protokol omogućuje računalu da prima podatke o proizvodnji solarne energije i potrošnji, te da šalje naredbe inverteru prema potrebi.
Naredba “QPIGS” koristi se za dobivanje informacija o trenutnom stanju invertera. Ova naredba šalje se kontinuirano pomoću alata za raspoređivanje zadataka (scheduler). Na taj način, računalo redovito ažurira informacije o stanju invertera.
Na gornjoj slici prikazane su sve ključne informacije o solarnoj stanici. U donjem lijevom kutu nalazi se grafički prikaz koji vizualizira podatke, dok se u donjem desnom kutu nalazi popis svih aktivnih pametnih prekidača.

<h2>Smart devices prozor</h2>
U ovom prozoru ispisana je lista pametnih prekidača. Na svaki prekidač spojeno je trošilo sa određenom potrošnjom i saki prekidač ima maksimalnu snagu koju može prenijeti.
Elementi jednog trošila u listi su:

- Točka - vizualni indikator koji pokazuje je li uređaj online, tj. ima li aktivnu internetsku vezu
- Ime uređaja
- Button Status koji predstavlja je li uređaj upaljen ili ne
- Button Activation koji stavlja prekidač u red za automatsko paljenje i gašenje
- Priority - prioritet kod automatskog paljenja/gašenja
- Power - vrijednost snage trošila (ako prekidač ima mogućnost mjerenja potrošnje, vrijednost se mijenja u stvarnom vremenu)

Za svaki prekidač moguće je prilagoditi maksimalno opterećenje i snagu trošila koja je na njega spojena. Također, prekidači mogu biti i dualni, što znači da mogu upravljati s dva odvojena trošila. U tom slučaju potrebno je specificirati ime i snagu svakog pojedinačnog izlaza (trošila) povezanog s prekidačem.

![SmartDevice edit](https://github.com/user-attachments/assets/e3580673-1021-4eed-a6ff-de55a54e8589)

Automatska kontrola potrošnje energije

Sustav automatske kontrole potrošnje energije funkcionira na temelju dinamičkog upravljanja prekidačima koji su povezani s različitim uređajima. Nakon što se neki prekidač aktivira, on se stavlja u listu "Ready". Ova lista se automatski sortira prema prioritetu, što znači da se prekidači s većim prioritetom nalaze na vrhu liste.
Kada sustav detektira da je dostupno dovoljno energije, prvi prekidač s vrha liste "Ready" se automatski uključuje i premješta u listu "Active", koja sadrži sve trenutno uključene uređaje. Na taj način sustav osigurava da su prioritetni uređaji prvi uključeni kada je dostupna energija.
Ako u nekom trenutku više nema dovoljno energije za održavanje svih aktivnih uređaja, zadnji prekidač iz liste "Active" se isključuje, čime se oslobađa energija za druge uređaje s većim prioritetom.
Ako je prvi prekidač u listi "Active" uključen, ali nema dovoljno energije za njegov rad, sustav ga isključuje i provjerava mogu li se ostali prekidači iz liste "Ready" uključiti s raspoloživom energijom. U tom slučaju, prekidači s manjim energetskim zahtjevima mogu biti aktivirani, dok se prekidač s najvećim prioritetom isključuje.

<h2>Settings</h2>

![settings](https://github.com/user-attachments/assets/a3d2d97a-7454-47dd-8b0b-89c4c8b0f0c0)

Rates (Brzine)
- U ovom dijelu se mogu prilagoditi intervali za različite funkcije aplikacije. To uključuje intervale skeniranja uređaja, učestalost spremanja podataka tijekom dana i noći, te intervale ažuriranja i resetiranja grafa
  
Smart Devices (Pametni uređaji)
- Ovdje se mogu podesiti parametri vezani uz pametne uređaje, poput maksimalne struje i snage koju kombinacija svih uređaja mogu koristiti. Također možete postaviti vrijeme u danu kada se izvršava automatsko uključivanje i isključivanje uređaja.
  
Account (Račun)
- Ovaj dio omogućuje pristup Ewelink računu. Također, se može odrediti I/O port na koji se spaja inverter.

