## Alfresco Transformer from Markdown to PDF

This project includes a sample Transformer for Alfresco from Markdown to PDF to be used with ACS Community 6.2+

The Transformer `ats-transformer-markdown` uses the new Local Transform API, that allows to register a Spring Boot Application as a local transformation service.

As this new API is still not integrated with Share Web App and SOLR, an additional repository behaviour `markdown-rendition` is provided. So when a Markdown content node is created or updated the Rendition Service V2 is fired.

Finally, a Docker Compose template is provided in `docker` folder to test all these components together.

```
.
├── README.md
├── ats-transformer-markdown
├── docker
│   ├── alfresco
│   ├── config
│   ├── docker-compose.yml
│   └── rendition-defs-markdown.json
└── markdown-rendition
```

* `ats-transformer-markdown` contains a Markdown Transformer using the new Local Transform API available from ACS 6.2
* `docker` contains a Docker Compose template to deploy the transformer and the behaviour in ACS Community 6.2
* `markdown-rendition` contains a behaviour to fire Rendition Service V2 when a Markdown content node is created or updated

## Build Docker Image for ATS Transformer Markdown

```
$ cd ats-transformer-markdown

$ mvn clean package

$ docker build . -t alfresco/ats-transformer-markdown:1.0
```

## Build sample behaviour for renditions in Share

```
$ cd markdown-rendition

$ mvn clean package
```

## Running with ACS CE 6.2

```
$ cd docker

$ docker-compose up --build --force-recreate
```

## Testing

A sample web page has been created in order to test the transformer is working:

http://localhost:8096

## Known issues

This rendition is not working by default for **Share** and **Alfresco Content Application** renditions, as these applications are using Transform REST API v0 (synchronous).

Anyway, renderization can be forced by using manually Transform Rest API V1 on every Markdown node:

```
$ curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'Authorization: Basic YWRtaW46YWRtaW4=' -d '{"id":"pdf"}' 'http://127.0.0.1/alfresco/api/-default-/public/alfresco/versions/1/nodes/09359434-5fc5-4e1d-8125-66101747c6e5/renditions'
```

Just be sure to use **id** as **pdf** in this request, that is the value expected by Share to find the renditioned node.

**Note** In this sample a new Behaviour to run Rendition Service V2 when using Share Web App has been added as a behaviour living in `markdown-rendition` project. It's deployed by default with Docker Compose in `docker` folder.

## Reference

https://github.com/Alfresco/acs-packaging/blob/master/docs/custom-transforms-and-renditions.md
