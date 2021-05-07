#!/bin/bash
apt-get update -y
apt-get install apt-transport-https ca-certificates wget dirmngr gnupg software-properties-common -y
wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add -
add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/
apt-get update -y
apt-get install adoptopenjdk-8-hotspot -y
apt-get install -y iptables
iptables -I INPUT ! -s &bungee_ip& -p tcp --dport 25565 -j DROP
apt-get install -y curl
curl -L https://www.dropbox.com/s/dcsunk9onmgqi9p/spigot-1.8.8.jar?dl=1 -o server.jar
mkdir plugins
curl -L https://www.dropbox.com/s/mp9xja8cbptvikj/bukkit.yml?dl=1 -o ./bukkit.yml
curl -L https://www.dropbox.com/s/vv78gh5j11o80a6/server.properties?dl=1 -o ./server.properties
curl -L https://www.dropbox.com/s/40gobxyjy5r6lok/spigot.yml?dl=1 -o ./spigot.yml
curl -L https://www.dropbox.com/s/9k1hntgiyw4epmm/werewolfplugin-1.7-SNAPSHOT.jar?dl=1 -o ./plugins/plugin.jar
echo "eula=true" > eula.txt
java -Xms&ram&G -Xmx&ram&G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar ./server.jar
