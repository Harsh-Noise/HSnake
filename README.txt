Welcome to HSnake!

HSnake is my take on the classic Snake type game, with custom levels, enemies, and leaderboards! Coded in Java.

- Disclaimer
    All files are licensed under the MIT License, except audio who's copyrights belongs with their respective copyright holders. HSnake is an educational and entertainment project for myself and my own interest. Support is not guarenteed. AI was used where it was needed.

- Compilation
    This project requires Maven to compile. Once installed simply run the command "mvn verify" in the project's root folder (this folder with the readme in it :P). The compiled executable will be exported to target/HSnake.jar. To run, type the command "java -jar HSnake.jar"
    I am currently in the packaging and distribution stage of development. There is an experimental RPM of the project that will compile itself in target/pkg automatically.

- Common issues
    >java.awt.HeadlessException
    This was a frequent issue on Fedora, which ships a headless version of Java (No GUI, audio, video libraries. Check your installed packages for something like java-25-openjdk-headless.) HSnake is built on Java's Swing GUI toolkit, and requires a full (headfull?) JDK.
    "sudo dnf install java-25-openjdk" should do the trick on Fedora based systems. For others I am certain similar packages exist, and should be easy to find.

    >no main manifest attribute
    Make sure you are running HSnake.jar. Maven will export a similar file named "SnakeGame-1.0-SNAPSHOT.jar" but this is an incomplete remnant of the packaging process with no link to the main class (where the code should start running from.)

    >java.lang.UnsupportedClassVersionError
    This may indicate you have an outdated version of Java. HSnake was compiled against Java 17, and the JRE is prefectly backwards compatable. As long as you have a JDK version of 17 or higher you should not face this error. Run "java -version"
