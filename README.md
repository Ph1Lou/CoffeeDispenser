# Coffee Dispenser

Allow launching server in the cloud, bind with a bungee-cord proxy

## Initialization

1. Put Coffee Dispenser in the bungee plugins folder
2. Launch the Bungee to generate the CoffeeDispenser config.yml then close it.
3. Fill the config.yml
4. Complete the userdata.sh this is the default initialization script of the digital ocean server.

5. The plugin is configured

## Use

### For server user

1. To launch a server (it will close after 3h55m automatically for security)

```/server-start```

2. To close a server

```/server-stop```

### For developer

1. From a bungee plugin called the event LaunchServerEvent

```
LaunchServerEvent launchServerEvent = new LaunchServerEvent(
                "FRA1",
                "debian-10-x64",
                "userdata.sh",
                4096,
                UUID.randomUUID(),
                (uuid) -> System.out.printf("Hello Server %s start",uuid.toString()),
                (uuid,reason) -> System.out.printf("Hello Server %s don't start because %s",uuid.toString(),reason));
                
getProxy().getPluginManager().callEvent(launchServerEvent);
```

2.The server will take about 3 minutes to start and set up


4. To close the server it is necessary to call the CloseServerEvent

```
getProxy().getPluginManager().callEvent(new CloseServerEvent(serverUuid));
```
