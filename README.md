# Лабораторна робота №1 — Система управління автосервісом

Відкрити `pom.xml` у IntelliJ IDEA (File → Open → вибрати `pom.xml` → Open as Project), відкрити термінал (View → Tool Windows → Terminal) і ввести:

```bash
mvn compile exec:java      # компілює проєкт і запускає CarServiceDemo
```

Або без Maven:

```bash
javac -d out src/*.java && java -cp out CarServiceDemo
```

Або просто запустити `src/CarServiceDemo.java` зеленою кнопкою ▶ біля методу `main`.
