# Spring Boot based efficient offline geo coding service
------------------------------------------------------
- Based on latest geonames.org data
- Very fast reverse geo coding
- Highly compressed index file
- Possibility to regenerate custom index with excluding countries (wip)
- Easy to use API 

This implementation is used within the project **Bluecate** for mapping bluetooth low energy geo position to tracking information.

### Parametres
- latitude
- longitude
- maxDistance (cell radius in search for neighbour places - default 1500m)
- maxHits (default 50)

### Usage example with json response

http://localhost:8087/geo-reverse-service/api/geocode?latitude=50.941638&longitude=6.958975&maxDistance=3000&maxHits=5

![GEO-REVERSE-SERVICE](https://raw.githubusercontent.com/jbellic/geo-reverse-service/master/geo-reverse-service.png)

### Cloning GIT Repository

```
git clone https://github.com/jbellic/geo-reverse-service.git
```


