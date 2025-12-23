environment status

java -version
java version "17.0.12" 2024-07-16 LTS
Java(TM) SE Runtime Environment (build 17.0.12+8-LTS-286)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.12+8-LTS-286, mixed mode, sharing)

kotlin -version
Kotlin version 2.3.0-release-356 (JRE 17.0.12+8-LTS-286)

gradle -version

------------------------------------------------------------
Gradle 9.2.1
------------------------------------------------------------

Build time:    2025-11-17 13:40:48 UTC
Revision:      30ecdc708db275e8f8769ea0620f6dd919a58f76

Kotlin:        2.2.20
Groovy:        4.0.28
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  17.0.12 (Oracle Corporation 17.0.12+8-LTS-286)
Daemon JVM:    C:\files\jdk-17.0.12 (no JDK specified, using current Java home)
OS:            Windows 10 10.0 amd64

git --version
git version 2.52.0.windows.1

to launch

gradle wrapper --gradle-version 8.9

------------------------------------------------------------
Gradle 8.9
------------------------------------------------------------

Build time:    2024-07-11 14:37:41 UTC
Revision:      d536ef36a19186ccc596d8817123e5445f30fef8

Kotlin:        1.9.23
Groovy:        3.0.21
Ant:           Apache Ant(TM) version 1.10.13 compiled on January 4 2023
Launcher JVM:  17.0.12 (Oracle Corporation 17.0.12+8-LTS-286)
Daemon JVM:    C:\files\jdk-17.0.12 (no JDK specified, using current Java home)
OS:            Windows 10 10.0 amd64


gradlew clean build bootRun

http://localhost:8081/ahead/swagger-ui.html
http://localhost:8081/ahead/v3/api-docs