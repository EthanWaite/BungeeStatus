# BungeeStatus
BungeeStatus is a very simple BungeeCord plugin that communicates with Mojang's public JSON API in order to retrieve the server status. If a service is offline, a message is broadcasted through the BungeeCord proxy. The format of these messages are configurable in `config.yml`, as well as the interval between each check.

You can compile this with Maven by downloading the files and then running `mvn package` within the directory.