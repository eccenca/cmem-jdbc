# cmem-jena-http-client

This project allows to SPARQL query CMEM using one of the most used RDF frameworks JENA by adding support to OAUTH2.


## Usage

1) Setup your credentials:

### Client Credentials

```
CMEMOAUTH2Authenticator authenticator = new CMEMOAUTH2Authenticator()
					.clientId(clientId)
					.clientSecret(secret)
					.grantType(GrantType.CLIENT_CREDENTIALS);
			connectionBuilder = new CMEMOAUTH2RemoteRequestBuilder(authenticator)
		            .host(endpoint);

```

or

### Password

```
CMEMOAUTH2Authenticator authenticator = new CMEMOAUTH2Authenticator()
					.clientId(clientId)
					.user(user)
					.password(secret)
					.grantType(GrantType.PASSWORD);
			connectionBuilder = new CMEMOAUTH2RemoteRequestBuilder(authenticator)
		            .host(endpoint);
```

2) Execute a SPARQL query:

```
 try (RDFConnection conn = connectionBuilder.build()) {
	  try(QueryExecution qExec = conn.query(query);) {
	    ResultSet rs = qExec.execSelect();
	    while (rs.hasNext()) {
	 				QuerySolution qs = rs.next();
          ...
	 		}
	  }
}
```


