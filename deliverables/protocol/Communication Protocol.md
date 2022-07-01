# Protocollo di comunicazione

Ogni tipo di messaggio corrisponde ad una classe Java: tutte le classi ereditano da un’unica classe astratta `Message`,
ed i messaggi sono serializzati tramite Java nella comunicazione basata su TCP che, in generale, segue uno schema richiesta-risposta.

## Messaggi _client_

### Generali

| Classe				| Significato										| Contenuto						|
| :---					| :---												| :---							|
| `Handshake`			| Inserimento nome utente							| _username_					|
| `Reconnect`			| Richiesta per riconnessione ad una partita		| `id` partita, _passcode_		|
| `HelpRequest`		| Richiesta per lista di comandi					| 								|

### Impostazione della partita

| Classe				| Significato								| Contenuto										|
| :---					| :---										| :---											|
| `LobbiesRequest`		| Richiesta per lista partite disponibili	| 												|
| `LobbyCreation`		| Richiesta per creazione nuova partita		| numero giocatori, _flag_ modalità esperto		|
| `JoinLobby`			| Richiesta per entrare in una partita		| `id` partita									|
| `LeaveLobby`			| Richiesta per uscire da una partita		| 												|
| `GameSetupSelection`	| Selezione colore torre e mago				| colore torre, mago							|

### Turno del giocatore

| Classe						| Significato								| Contenuto								|
| :---							| :---										| :---									|
| `PlayAssistantCard`			| Selezione carta assistente				| carta assistente						|
| `MoveStudent`				| Spostamento student						| colore e destinazione per studente	|
| `MotherNatureDestination`	| Movimento della pedina Madre Natura		| isola di destinazione					|
| `SelectCloud`				| Prelevamento studenti da nuvola			| nuvola								|
| `PlayCharacterCard`			| Attivazione carta personaggio				| carta personaggio, parametri			|

## Messaggi _server_

### Generali

| Classe						| Significato										| Contenuto																				|
| :---							| :---												| :---																					|
| `Accepted`					| Risposta a fronte di richiesta valida				| 																						|
| `AcceptedUsername`			| Accettazione di nome utente						| _username_																			|
| `AcceptedJoinLobby`			| Accettazione per ingresso in partita				| `id` partita, _passcode_																|
| `AcceptedLeaveLobby`			| Accettazione per uscita da partita				| 																						|
| `Refused`					| Risposta a fronte di richiesta non valida			| 																						|
| `RefusedReconnect`			| Riconnessione non disponibile						| 																						|
| `DisconnectionUpdate`		| Notifica disconnessione di un giocatore			| _username_ giocatore, numero di giocatori connessi, _flag_ stato partita (pausa)		|
| `ReconnectionUpdate`			| Notifica riconnessione di un giocatore			| _username_ giocatore, numero di giocatori connessi, _flag_ stato partita (pausa)		|
| `HelpResponse`				| Risposta con lista di comandi						| comandi																				|
| `Ping`						| Controllo periodico dello stato della connessione	| 																						|

### Impostazione della partita

| Classe						| Significato										| Contenuto														|
| :---							| :---												| :---															|
| `AvailableLobbies`			| Richiesta per lista partite disponibili			| partite														|
| `LobbyUpdate`				| Notifica entrata od uscita di un giocatore		| giocatori														|
| `UserSelectionUpdate`		| Notifica selezioni torri e maghi					| torri disponibili, maghi disponibili, prossimo giocatore		|
| `InitialBoardStatus`			| Invio stato iniziale del campo di gioco			| stato iniziale												|

### Turno del giocatore

| Classe						| Significato								| Contenuto																		|
| :---							| :---										| :---																			|
| `AssistantCardUpdate`		| Notifica selezioni carte assistente		| carte assistente disponibili e giocate precedentemente, prossimo giocatore	|
| `BoardUpdate`				| Aggiornamento del campo di gioco			| stato aggiornato, prossimo giocatore											|
| `CharacterCardUpdate`		| Notifica attivazione carta personaggio	| carta																			|
| `LastRoundUpdate`			| Notifica inizio ultimo _round_			| 																				|
| `GameOverUpdate`				| Notifica fine partita						| vincitori																		|