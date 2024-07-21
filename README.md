## RCS Stocks

An application for following live stock prices. 

Features:

- Authentication
- WebSockets
- Searching for stocks

<hr>

#### Tech stack

Backend: Java, SpringBoot, MySQL, MongoDB

Frontend: React, TypeScript, Redux

<hr>

#### Dependencies

* [Docker](https://www.docker.com/)

<hr>

#### Configure

Create a `.env` file in `back/stock-service` containing the Finnhub token:

<pre>
SERVICES_FINNHUB_TOKEN=[token]
</pre>

<hr>

#### Build & run

<pre>
./start-back.sh & ./start-front.sh
</pre>

For development, it's better to run the frontend outside Docker:

<pre>
./start-front-dev.sh
</pre>

- Frontend: http://localhost:3000

- Auth-service: http://localhost:8080

- Stock-service: http://localhost:8081

<hr>

#### Run integration tests

Auth-service:
<pre>
./run-auth-service-it-tests.sh
</pre>

Stock-service:
<pre>
./run-stock-service-it-tests.sh
</pre>

<hr>

#### Built with

This project was entirely written by me and built by piecing together some of my other repositories:

- [authenticated-fullstack-starter](https://github.com/raphael-correa-ng/authenticated-fullstack-starter)
- [RcsStockAppBack](https://github.com/raphael-correa-ng/RcsStockAppBack)
- [RcsStockAppFront](https://github.com/raphael-correa-ng/RcsStockAppFront)
- [Tapestrie](https://github.com/raphael-correa-ng/Tapestrie)


<hr>

#### Other features

The Docker configurations here implement a way to use local Maven dependencies during build. 
See `back/DockerFileWithDepedency`.

In oder for this to work, the Docker files needed to be directly under `back/`, not inside each microservice, because Docker does not allow accessing files outside the working directory.

This is a valid approach, but in production, it's recommended to use a remote artifact repo, such as Artifactory.

<hr>

#### Notes

To best work on this project on Intellij, import the `front/` and `back/*` directories as modules.

Do: 

*File > New > Module From Existing Sources...*

For each:
- `front/` (whole directory)
- `back/auth/pom.xml`
- `back/auth-api/pom.xml`
- `back/stock-service/pom.xml`

Then, to access the Docker files in `back/`, you need to use the "Project Files" view. It's not perfect, but it's one way to keep the entire project in one IDE window.

If Intellij doesn't find the Maven dependencies, do `File > Invalidate Caches > Invalid and restart`.

MySQL wouldn't start on Docker, solved by [this](https://stackoverflow.com/questions/77344634/azerothcore-docker-install-db-fails-with-upgrade-is-not-supported-after-a-cras).
