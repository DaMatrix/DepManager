# DepManager

[![Build Status](http://jenkins.daporkchop.net/job/Minecraft/job/DepManager/badge/icon)](http://jenkins.daporkchop.net/job/Minecraft/job/DepManager/)
[![Discord](https://img.shields.io/discord/428813657816956929.svg)](https://discord.gg/FrBHHCk)

DepManager is a Minecraft Forge mod that allows your mod to depend on Maven artifacts without having to shade them into your main jar, therefore reducing a lot of pointless bloating.

## For developers

Using DepManager is really easy. Just make a file called `<whatever you like>.depmanager.json` in your resources folder, or really anywhere that will result in the file being copied into your output JAR's root.
The contents of this file should be formatted like this:

```json
{
    "name": "Cool mod!",
    "dependencies": [
        {
            "groupId": "com.flowpowered",
            "artifactId": "flow-network",
            "version": "1.0.0",
            "classifier": ""
        },
        { //this will be downloaded from curseforge, https://authors.curseforge.com/docs/api
            "groupId": "cofhcore",
            "artifactId": "CoFHCore-1.12.2",
            "version": "release",
            "classifier": "universal"
        },
        {
            "groupId": "net.daporkchop.lib",
            "artifactId": "crypto",
            "version": "0.1.1",
            "classifier": ""
        }
    ],
    "repositories": [
        {
            "id": "DaPorkchop_'s repo",
            "url": "https://maven.daporkchop.net/",
            "signature": "GRJ4VwGL3yL7owdMKkgQ9NdWBX8m5qyhx4DM9ftD7yhXnT3XxLEoiJKNodpCWVvx2c7FpdEEaQdLfhyuJX9FKPQHCKxjx8VK1ZAV36qN43HZagCPgeAYbipgXapyJnrj74KD3aUbQDuWeB5Sf83jxTpUMST3BDKo9hzEhMmxtYsjPjc"
        }
    ]
}
```

In order to prevent users from uploading malicious artifacts to their own repository, adding it to the repositories section and having other people download those malicious libraries, DepManager uses an elliptic curve (ECDSA) signature for each repository. A list of signatures for public repositories can be found below. If somebody adds a repository without a [valid] signature, the user will be prompted whether or not to trust it.  
Note: it is important to include the trailing slash on the URL!

### Official repositories

| Name                         | URL                                                     | Signature                                                                                                                                                                       | Note                                                                                                                     |
|------------------------------|---------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| Maven Central                | https://repo.maven.apache.org/maven2/                   | 6STAjimZBb5Zd8YpBJ6NDLQxnBUUec5nyHq7JPK9SA11x4NqZjcLKXLNphf97fH4d4YCQFtxrQLB21RWuc6p1j9Ff8jg35SrBaKVGLeTWsH4HjbMpYERzqAm1TxoK5JMya82xKHqTNTVQ1CZxDkZjqPtnMQoGJrX1HTdNfonvLby65M | It is not advised to use this repository for production, as Central isn't designed to deal with large amounts of traffic |
| JCenter                      | https://jcenter.bintray.com/                            | CaCf5nQcx6nPXyLJcszry5dewKAQP3RXGL1rmWE4FQWHJp3SDcs5B8YUneTghxCNxGQ7zHbUJtACMUivWNpN7kHLoT6NFVuXswq42nVdSqhQn92gUG3U1SvUYmBYKDQLQCuhuBM5qw9dCvTTJ8a3Bvvr7JDEX23JU6JVrXaeL3G22ac |                                                                                                                          |
| Sonatype                     | https://oss.sonatype.org/content/repositories/releases/ | 4EPjwNQSCvBuchTzRc1StAxwSQknNw2pgvZrHBBfAJBzGGp9eArFkiouAWFth5zj3faaAQf37LvreznFMmCt8ECsAjcA2KiWqLGuoPxBMZYSgZhb9y96pKXPzXHyd55GdjuTjxeuiH2aAFEnUqBMzGKH4jyX8nvMY5HBCh5e1sF9qKd |                                                                                                                          |
| DaPorkchop_'s Maven server   | https://maven.daporkchop.net/                           | GRJ4VwGL3yL7owdMKkgQ9NdWBX8m5qyhx4DM9ftD7yhXnT3XxLEoiJKNodpCWVvx2c7FpdEEaQdLfhyuJX9FKPQHCKxjx8VK1ZAV36qN43HZagCPgeAYbipgXapyJnrj74KD3aUbQDuWeB5Sf83jxTpUMST3BDKo9hzEhMmxtYsjPjc |                                                                                                                          |
