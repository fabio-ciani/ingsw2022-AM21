# _Peer-Review_: protocollo di comunicazione

Gruppo AM21:
* **Tommaso Bonetti**
* **Fabio Ciani**
* **Davide Mozzi**

La _peer-review_ è articolata in una valutazione del protocollo di comunicazione (_client-server_) del gruppo AM51.

## Lati positivi

### Suddivisione concettuale dei livelli

Individuare e separare due _layers_ permette di verificare lo stato delle connessioni e
gestire opportunamente disconnessioni senza interferire con la principale infrastruttura
per messaggi di gioco.

### Categorizzazione delle partite

Le partite sono classificate in:
* partite salvate e disponibili alla riapertura;
* partite salvate in fase di riapertura;
* partite create da terzi in attesa di giocatori.

Oltre a ciò, un utente ha la possibilità di creare una nuova partita.

Riteniamo che tale ripartizione risulti particolarmente efficace ed accorta
per semplificare il flusso di controllo per gestione dei messaggi _server-side_ e UX.

### Aggiornamento delle viste _client-side_

L'invio di un _update_ per ciascuna mossa di un giocatore facilita il mantenimento _up-to-date_ delle _views_.

## Lati negativi

### Mancanza di un _ping client-side_

A fronte di un problema di connessione, un _client_ potrebbe impiegare molto tempo
per accorgersi della propria disconnessione.

## Confronto tra le architetture

L'architettura è molto simile all'idea del nostro gruppo, anche per via di una sostanziale corrispondenza tra i messaggi definiti.
Il concetto di salvataggio di una partita assume due significati distinti per i due gruppi:
* nel gruppo AM21, è resa possibile la riconnessione di un _client_ per una partita ancora in corso;
* nel gruppo AM51, una partita è manipolata mediante un processo di persistenza.