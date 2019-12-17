## Alfresco Transformer from Markdown to PDF

This project includes a sample Transformer for Alfresco from Markdown to PDF to be used with ACS Community 6.2+

The Transformer `ats-transformer-markdown` uses the new Local Transform API, that allows to register a Spring Boot Application as a local transformation service.

As this new API is still not integrated with Share Web App and SOLR, an additional repository behaviour `markdown-rendition` is provided. So when a Markdown content node is created or updated the Rendition Service V2 is fired.

Finally, a Docker Compose template is provided in `docker` folder to test all these components together.

```
.
├── ats-transformer-markdown
├── docker
│   ├── alfresco
│   ├── docker-compose.yml
│   └── rendition-defs-markdown.json
└── markdown-rendition
```

* `ats-transformer-markdown` contains a Markdown Transformer using the new Local Transform API available from ACS 6.2
* `docker` contains a Docker Compose template to deploy the transformer and the behaviour `markdown-rendition` in ACS Community 6.2
* `markdown-rendition` contains a behaviour to fire Rendition Service V2 when a Markdown content node is created or updated

## Build Docker Image for ATS Transformer Markdown

Building the ATS Transformer Markdown Docker Image is required before running the Docker Compose template provided.

```
$ cd ats-transformer-markdown

$ mvn clean package

$ docker build . -t alfresco/ats-transformer-markdown:1.0
```

## Build sample behaviour for renditions in Share

Sample behaviour JAR Alfresco Module is provided by default in `docker` folder, so it's not required to build it before trying the sample. Anyway, this module can be built using default Maven command.

```
$ cd markdown-rendition

$ mvn clean package
```

## Running with ACS CE 6.2

> Be sure that ATS Transformer Markdown Docker Image has been built before starting Docker Compose.

```
$ cd docker

$ docker-compose up --build --force-recreate
```

## Testing

A sample web page has been created in order to test the transformer is working:

http://localhost:8096

A markdown file can be uploaded in order to get the transformed PDF file.

## Known issues

This rendition is not working by default for **Share** and **Alfresco Content Application** renditions, as these applications are using Transform REST API v0 (synchronous).

Anyway, renderization can be forced by using manually Transform Rest API V1 on every Markdown node:

```
$ curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' -d '{"id":"pdf"}' \
'http://127.0.0.1/alfresco/api/-default-/public/alfresco/versions/1/nodes/09359434-5fc5-4e1d-8125-66101747c6e5/renditions'
```

Just be sure to use **id** as **pdf** in this request, as that is the value expected by Share to find the renditioned node.

**Note** In this sample a new Behaviour to run Rendition Service V2 when using Share Web App has been added as an standard Alfresco module living in `markdown-rendition` project. It's deployed by default with Docker Compose in `docker` folder, so renditions will be applied to Markdown content nodes uploaded or modified in Share Web App.

## Reference

Additional information can be found in:

https://hub.alfresco.com/t5/alfresco-content-services-blog/quick-reference-for-transformers-in-acs-community-6-2/ba-p/294824
