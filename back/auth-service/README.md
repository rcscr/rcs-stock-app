## `auth-service`


#### Build & run

<pre>
cd .. && docker-compose -f docker-compose-auth-service.yml up --build
</pre>

The Docker files are stored in the parent folder so that it has access to the `auth-api` module.

<hr>

##### Register

<pre>
curl -X POST host:port/register -d "username=USERNAME&password=PASSWORD"
</pre>

##### Login

<pre>
curl -X POST host:port/login -d "username=USERNAME&password=PASSWORD" -c cookies
</pre>
