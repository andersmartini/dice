#UrlShortener By Anders Martini

##Introduction
This project uses DynamoDB as a backend to store a relation between shortened URL's and their full counterparts.
Shortened Url's are derived from a MD5 hash, but using only the first 10 chars to keep it short.

Using a hashingalgorithm like MD5 ensures in a simple way that any given URL will get the same shortened url each time,
while storing the relation in DynamoDB offloads the most difficult part of scalability to Amazon AWS.

The Code itself is written in the Micronaut framework, which is highly resource efficient, especially when, as in this case,
it is combined with RxJava. RxJava provides Asynchronous IO, vastly improving the amount of load any given instance of the application
can handle.

##Testing the application
You have 2 options for testing this application. Unit-tests are provided and can be run either from your favourite IDE
or via gradle build. it should be noted that the database is mocked in this case

You other option is to run a local instance of DynamoDB, configure the application to target this local instance 
and set up credentials in a proper way. Just kidding - just execute run.sh ;).

##The API
The application will start on port 8080 and has 2 endpoints. to create a shortened URL you can run the following:
`curl localhost:8080/shorten -d "https://andersmartini.com" -H "Content-Type: application/json"`

it will return a string looking something like this: `localhost:8080/dc87807a5`

you can either paste that into your browser or curl it. if you use curl, remember to use the --location flag:

`curl --location localhost:8080/dc87807a5` 


##Discussion
This application uses a substring of a MD5 hash to generate shortened URLs and store them in a database. An alternative
solution would be to use some sort of compression-algorithm to compress strings, and decompress them. This has the benefit
of not requiring a database, and so is cheaper and easier to scale. However, it is unlikely to be able to shorten url's to 
as short a format as this application is doing, and certainly not static length as we do here: it would instead shorten 
the url's by some percentage of their original length.

