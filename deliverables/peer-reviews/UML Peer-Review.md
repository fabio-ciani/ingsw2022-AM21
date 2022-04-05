# _Peer-Review_: UML

Gruppo AM21:
* **Tommaso Bonetti**
* **Fabio Ciani**
* **Davide Mozzi**

La _peer-review_ è articolata in una valutazione del diagramma UML delle classi (_class diagram_) del gruppo AM51.

## Lati positivi

### `StudentContainer`

Moltissimi oggetti di gioco contengono dischi studente,
perciò definire un'astrazione comune (interfaccia) rende omogeneo il _design_
ed uniforma i metodi esposti dalle classi che modellano questi oggetti.
Nonostante siano sottolineabili alcuni aspetti negativi riguardo la struttura interna
delle classi che implementano l'interfaccia, i vantaggi compensano decisamente le criticità.

### `TurnState`

Concettualizzare le azioni da svolgere in un certo momento del turno mediante
una enumerazione aiuta a modellare in maniera fedele il flusso di gioco.

### Gestione del _game over_

L'uso di un attributo `boolean lastRound` permette di gestire
efficientemente un caso limite in cui un ultimo _round_ debba essere
giocato per intero precedentemente alla selezione del vincitore.

## Lati negativi

### Carte personaggio

Il principale aspetto negativo individuabile consta nell'istanziare
la medesima classe `StudentMoverCharacterCard` per personaggi diversi.
Ogni oggetto contiene tutti gli `StudentMover` in grado di gestire i personaggi atti allo spostamento di studenti:
ciò implica un approccio consistentemente procedurale (i.e., individuazione del corretto elemento in `List<StudentMover>`).
Sarebbe auspicabile differenziare maggiormente l'implementazione di ciascun personaggio.

### Implementazione di `StudentContainer`

Sono presenti due criticità.
1. Il fatto che `StudentContainer` sia un'interfaccia obbliga le classi che la implementano
a riscrivere i suoi metodi, comportando diversi frammenti di codice duplicato.
2. È sconsigliabile l'uso di un _array_ di interi che, in tal scenario, 
non è particolarmente conforme al paradigma OOP:
questa rappresentazione costringe ad avere una corrispondenza _hard-coded_
tra `Clan` e indici dell'_array_.

## Confronto tra le architetture

Il _design_ del nostro progetto affida al _controller_ la gestione del flusso di gioco.
Quindi, usufruire di un meccanismo simile a `TurnState` potrebbe facilitarne l'amministrazione,
aiutando la comunicazione tra _view_ e _controller_ e semplificando le chiamate verso il _model_.

Infine, aggiungere un attributo come `boolean lastRound` potrebbe agevolare il modo in cui
sono valutate le condizioni di fine partita, dividendo questo compito tra
gli appositi metodi e lo stato del gioco.