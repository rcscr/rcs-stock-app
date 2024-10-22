## `stock-service`

#### Build & run

<pre>
cd .. && docker-compose -f docker-compose-stock-service.yml
</pre>

The Docker files are stored in the parent folder so that it has access to the auth-api module

<hr>

##### First, register on Auth service

<pre>
curl -X POST [AUTH_URL]/register -d "username=USERNAME&password=PASSWORD"
</pre>

##### Then login on Auth service to obtain the cookies

<pre>
curl -X POST [AUTH_URL]/login -d "username=USERNAME&password=PASSWORD" -c cookies
</pre>

##### Follow a stock

<pre>
curl -b cookies -X PUT host:port/my-stocks?stock=IBM
</pre>

##### Unfollow a stock

<pre>
curl -b cookies -X DELETE host:port/my-stocks?stock=IBM
</pre>

##### Get your stocks

<pre>
curl -b cookies -X GET host:port/my-stocks
</pre>

##### Search for stocks by symbol or description

<pre>
curl -b cookies -X GET host:port/stocks?search=MICRO&limit=10
</pre>
