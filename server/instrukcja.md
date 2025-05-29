# Instrukcja odpalenia backendu
1. Umieść plik ze zmiennymi środowiskowymi w katalogu /server/.env  . Plik ten otrzymałeś discordem
2. W pliku compose podaj ścieżki do plików wejściowych użytkowników i urządzeń. Pamiętaj zmienić tylko ścieżkę windowsową, nie nadpisuj tych używanych w kontenerze. Pliki te otrzymałeś oddzielnym komunikatorem
2. cd /Devops
3. docker compose up
4. Możesz przetestować czy backend wstał jak należy wysyłając get (lub wpisując w przeglądarkę) http://127.0.0.1:8080/actuator/health
