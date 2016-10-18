
#Ninja OAuth Template

This is a starting framework for using a [Ninja Web Framework](http://www.ninjaframework.org/) application, on [https://www.heroku.com](Heroku) that is ready to OAuth into a [https://www.salesforce.com](Salesforce) instance. 
It also has a dependency on [http://redis.io/](Redis) to handle some session stuff.  

## Get Started
As this kind of relies on Salesforce as an Identity Provider you probably need to setup a developer instance to handle the other end of the OAuth handshake. When I develop, I actually use two different developer environments so I can point one OAuth callback to my localhost environment, and a different callback to the Heroku instance when it is deployed. Of course, its perfectly OK to deploy directly to Heroku but I would recommend taking the time to setup locally as Ninja hot deploys quite a lot for you automagically, saves plenty of development time. 

For mine, I have a troy@project.local and a troy@project.org set of user names so I configure the OAuth twice... 

Details on how to configure a [https://help.salesforce.com/HTViewHelpDoc?id=connected_app_create.htm](Connected App) for Salesforce, however if you haven't read [https://developer.salesforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com](this developerforce post) on OAuth yet, I suggest you start there.  

From here we assume you have configured an OAuth environment to work with.

## Environment Variables
Like any good 12 Factor app, we want to externalise our application configuration so here is the environment variables you want to have set to run this codebase. 

| Variable | Purpose | Where to find it |
| -------- | ------- | ---------------- |
| application.secret | Part of the Ninja Framework in-built security | [http://www.ninjaframework.org/documentation/security/getting_started.html](Ninja Docs) |
| client_id | This Consumer Key value from your connected app | Setup -> Apps -> Your connected app |
| client_secret | This Consumer Secret value from your connected app | Setup -> Apps -> Your connected app |
| redirect_uri | This Callback URL value from your connected app | Setup -> Apps -> Your connected app | 

##Run Locally
If you want to run this project locally you will need a local version of Redis (or an accessible version of Redis somewhere)

Clone this repository then open the terminal.. 

If you are an eclipse user 
```
mvn eclipse:eclipse
```

Otherwise, you might just want to run from commandline... 
```



