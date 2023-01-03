# to_do_Lista
Aplikacja terminalowa do zarządania zadaniami. Projekt miał na celu posługiwanie się JDBC, program przystosowany do uruchomienia np w InteliJ ze względu na koniecznosc wykonania kilku kroków:

1.należy zainstalować baze danych postgreSql 

2.należy wpisać w querry tool następującą komende

create table TODOLIST(
	ID SERIAL NOT NULL PRIMARY KEY,
	NAME VARCHAR(20) NOT NULL UNIQUE,
	DESCRIPTION VARCHAR(128) NOT NULL,
	DEADLINE  TIMESTAMP WITH TIME ZONE,
	PRIORITY INT NOT NULL
);

3. w klasie DatabaseRunner w polu USERNAME należy wpisać podaną przy tworzeniu bazy danych nazwe użytkownika

4. w klasie DatabaseRunner w polu PASSWORD należy wpisać podane przy tworzeniu bazy danych nazwe hasło

5. uruchomić metodę main z klasy ScannerRunner

W projekcie użyłem technologię takie jak programowanie funkcyjne, programowanie obiektowe, jdbc, bazy danych postgreSql, Sql
