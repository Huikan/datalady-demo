# Datalady Meetup Demo 

This is a simple demonstration which includes two applications
- spark streaming application: run in local with 2 cores, max memory=512MB
- python application on top of flask-framework: receive the streaming data from streaming application

## Pre-requirement

- Java 8 
- Spark 2.3
- Python 2.7

To make the streaming application worked, since it uses twitter streaming API, you should follow the steps as indicated:
- twitter application sign in page [https://apps.twitter.com/](sign in page)

Run streaming application with the following options
```$bash
-Dtwitter4j.oauth.consumerKey=YOUR_CONSUMER_KEY
-Dtwitter4j.oauth.consumerSecret=YOUR_CONSUMER_SECRET
-Dtwitter4j.oauth.accessToken=YOUR_ACCESS_TOKEN
-Dtwitter4j.oauth.accessTokenSecret=YOUR_ACCESS_TOKEN_SECRET
```

### Spark streaming application - Top Hashtag 
- create application jar
`sbt package`

- submit application 
```
$SPARK_HOME/bin/spark-submit \
                             --class com.deezer.core.datalady.demo.HashTagConsumer \
                             YOUR_PACKAGE_JAR 
                             -Dtwitter4j.oauth.consumerKey=YOUR_CONSUMER_KEY \
                             -Dtwitter4j.oauth.consumerSecret=YOUR_CONSUMER_SECRET \
                             -Dtwitter4j.oauth.accessToken=YOUR_ACCESS_TOKEN \
                             -Dtwitter4j.oauth.accessTokenSecret=YOUR_ACCESS_TOKEN_SECRET 

                             
             
```


### Flask application - Chart visualization 
- create a virtual env, and activate it
- use pip to install the `requirements.txt`
- use this command to get application run
`flask run app.py` 
