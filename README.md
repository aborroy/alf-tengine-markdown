## Alfresco Transformer from Markdown to PDF

To be used with ACS Community 6.2+

## Build Docker Image

```
$ cd ats-transformer-markdown

$ mvn clean package

$ docker build . -t alfresco/ats-transformer-markdown:1.0
```

## Running with ACS CE 6.2

```
$ cd docker

$ docker-compose up --build --force-recreate
```

## Testing

http://localhost:8096
