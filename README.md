# Eriantys
An implementation of a tabletop game as the _Software Engineering_ final examination project at [Polytechnic University of Milan](https://www.polimi.it/).

## Development Team
* **Tommaso Bonetti** ([@tommaso-bonetti](https://github.com/tommaso-bonetti))
[tommaso2.bonetti@mail.polimi.it](mailto:tommaso2.bonetti@mail.polimi.it)
* **Fabio Ciani** ([@fabio-ciani](https://github.com/fabio-ciani))
[fabio1.ciani@mail.polimi.it](mailto:fabio1.ciani@mail.polimi.it)
* **Davide Mozzi** ([@davide-mozzi](https://github.com/davide-mozzi))
[davide.mozzi@mail.polimi.it](mailto:davide.mozzi@mail.polimi.it)

## Implemented features
| Functionality			| State				|
| :---						| :---:				|
| Simplified Mode			| :green_circle:	|
| Expert Mode				| :green_circle:	|
| CLI						| :green_circle:	|
| GUI						| :green_circle:	|
| Character Cards			| :green_circle:	|
| 4-Player Mode			| :red_circle:		|
| Multiple Games			| :green_circle:	|
| Persistence				| :red_circle:		|
| Disconnection Handling	| :green_circle:	|

## Usage

The project is built upon Java SDK 17.

### Server

A server can be instantiated with the following command.
```
java -jar Eriantys-Server.jar [--port PORT]
```
By default, the port on which the server runs is `9133`.
The `--port` optional argument can be replaced with the abbreviation `-p`.

### Client

A client can be instantiated with the following command.
```
java -jar Eriantys-Client.jar [--address IP] [--port PORT] [--interface TYPE]
```
By default:
* the IP address used for the TCP connection is `localhost`;
* the user-interface is of type GUI.

The `--address` optional argument can be replaced with the abbreviation `-addr`,
while the `--interface` optional argument with `-ui`.

## Tests coverage

The `model` and `controller` packages coverage are reported as follows.

| Package		| Class coverage	| Method coverage	| Line coverage	|
| :---			| :---				| :---				| :---			|
| `model`		| 100% (46/46)		| 88% (212/239)		| 86% (820/945)	|
| `controller`	| 100% (9/9)		| 97% (91/93)		| 81% (312/383)	|

The coverages regarding the subpackages are detailed in the table below.

| Main package	| Subpackage	| Class coverage	| Method coverage	| Line coverage	|
| :---			| :---			| :---				| :---				| :---			|
| `model`		| `characters`	| 100% (16/16)		| 95% (40/42)		| 98% (130/132)	|
| `model`		| `exceptions`	| 100% (7/7)		| 81% (9/11)		| 81% (9/11)	|
| `model`		| `influence`	| 100% (4/4)		| 100% (8/8)		| 94% (33/35)	|
| `controller`	| `phases`		| 100% (7/7)		| 97% (38/39)		| 74% (141/189)	|