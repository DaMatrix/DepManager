# DepManager

[![Build Status](http://jenkins.daporkchop.net/job/Minecraft/job/DepManager/badge/icon)](http://jenkins.daporkchop.net/job/Minecraft/job/DepManager/)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

DepManager is a Minecraft Forge mod that allows your mod to depend on Maven artifacts without having to shade them into your main jar, therefore reducing a lot of pointless bloating.

How to use:

Using DepManager is really easy. Just make a file called `<whatever you like>.depmanager.json` in your resources folder, or really anywhere that will result in the file being copied into your output JAR's root.
The contents of this file should be formatted like this:

```json
{
    "name": "Cool mod!",
    "dependencies": [
        { //flow-network from Maven Central
            "groupId": "com.flowpowered",
            "artifactId": "flow-network",
            "version": "1.0.0"
        },
        { //CoFHCore from CurseForge
            "groupId": "cofhcore",
            "artifactId": "CoFHCore-1.12.2",
            "version": "release"
        },
        { //PorkLib Crypto from my own repository (see below)
            "groupId": "net.daporkchop.lib",
            "artifactId": "crypto",
            "version": "0.1.1"
        }
    ],
    "repositories": [
        {
            "id": "DaPorkchop_'s repo",
            "url": "http://maven.daporkchop.net/"
        }
    ]
}
```
