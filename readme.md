# Cat-As-A-Service

## About the project
Client-Server application is implemented in this
project. It uses two different protocols for
communication: UDP, TCP. These protocols use
two different ports on server. Output of given parameters
to the server is shown with Http page. You may see result
by address `http://5.42.220.74:8080/`.

## Functionality
**UDP SERVER**<br>
Server that uses protocol `UDP` is responsible
for opportunity to _feed cat with a specified dish_.
Server receives a datagram with UDP protocol, which contains
string with username and dish name.<br>
String should be represented in format: "@Alex - Milk~", "@007 - Martini~", "@Zavulon1237 - Meat~".
___
**TCP SERVER**<br>
TCP Server receives id of user in format "@username" and server gives a
possibility to stroke a cat if user name presents in _feeders list_ in other case
cat scratches user because they didn't treat cat before. Also cat may be tired
of too much attention, hence  it can run away. Server will close connection in that case.
___
**HTTP SERVER**<br>
Server Http is responsible for output of two lists, first one shows _feeders - dishes_ list and second
shows _strokers_ list.

## Cat documentation
List of preferred meals:
1. milk
2. fish
3. martini
4. biscuit
5. porridge
6. bear
7. coffee
8. ret
9. udon
10. dumplings
11. meat

Cat gives you chance to stroke it from 2 to 4 times,
depends on its mood.

## Launch
To launch client program install `tmpClientProj.jar` and install
java in case its absence.<br> And run the following command from directory where 
`tmpClientProj.jar` was installed: ```java -jar tmpClientProj.jar```.

## Author
Novikov Tim


