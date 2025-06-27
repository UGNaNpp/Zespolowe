# Dokumentacja oglądania zapisanego nagrania

## Informacje wstępne

### Przykładowe zapytania
Wszystkie wykorzystane tutaj przykłady zapytań znajdują się w załączonym pliku postmana, w folderze dokumentacji.

Jeśli backend stoi na localhost na porcie 3000 to

base_url = http://localhost:3000/

(Pamiętać o / na końcu base_url)

### Formatowanie dat
Nalezy używać dat formatowanych wg **ISO 8601**

Datetime oznacza datę i godzinę z dokładnościa do sekund 

## Odtworzenie nagrania w przeglądarce

**NIE DZIAŁA W POSTMANIE, WKLEJ DO PRZEGLĄDARKI PO WCZEŚNIEJSZYM USTAWIENIU CIASTECZEK PRZEZ ZALOGOWANIE DO FRONTU**:
  ```
{{base_url}}records/stream/<id kamery>/<datetime startu nagrania>/<Ilość klatek na sekundę>
  ```
Wybór prędkości odtwarzania pozwala na obejrzenie w przyspieszeniu

Przykład:
  ```
  {{base_url}}records/stream/8/2025-06-09T14:27:00/10
  ```

## Pobranie nagrania z określonego czasu na komputer
Należy wykonać 2 zapytania. Pierwsze po dłuższym czasie na render zwróci nazwę filmiku, którą należy podać w 2gim zapytaniu.

### Pierwsze zapytanie
**Użyj zapytania http typu POST**

Zamiast PathVariable używam RequestParams, szłoby się pogubić przy tylu argumentach

```
{{base_url}}records/video/save
```
No i parametry:
- cameraId
- stardDateTime
- stopDateTime
- frameRate


Przykład:
```
{{base_url}}records/video/save?cameraId=8&startDateTime=2025-06-27T14:23:00&stopDateTime=2025-06-27T20:29:00&frameRate=10
```
U mnie dokładnie ten przykład zwrócił string  "video_8_1751052188787.mp4"

### 2gie zapytanie
**Tutaj już zwykły GET**

```
{{base_url}}records/video/download/<nazwa pliku wcześniej otrzymana>
```

Przykład:
```
{{base_url}}records/video/download/video_8_1751052188787.mp4
```
