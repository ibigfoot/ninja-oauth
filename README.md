
#Ninja OAuth Template

This is a starting framework for using a [Ninja Web Framework](http://www.ninjaframework.org/) application, on [Herok](https://www.heroku.com) that is ready to OAuth into a [Salesforce](https://www.salesforce.com) instance. 
It also has a dependency on [http://redis.io/](Redis) to handle some session stuff.  

## Get Started
As this kind of relies on Salesforce as an Identity Provider you probably need to setup a developer instance to handle the other end of the OAuth handshake. When I develop, I actually use two different developer environments so I can point one OAuth callback to my localhost environment, and a different callback to the Heroku instance when it is deployed. Of course, its perfectly OK to deploy directly to Heroku but I would recommend taking the time to setup locally as Ninja hot deploys quite a lot for you automagically, saves plenty of development time. 

For mine, I have a troy@project.local and a troy@project.org set of user names so I configure the OAuth twice... 

Details on how to configure a [Connected App](https://help.salesforce.com/HTViewHelpDoc?id=connected_app_create.htm) for Salesforce, however if you haven't read [this DeveloperForce post](https://developer.salesforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com) on OAuth yet, I suggest you start there.  

From here we assume you have configured an OAuth environment to work with.

## Environment Variables
Like any good 12 Factor app, we want to externalise our application configuration so here is the environment variables you want to have set to run this codebase. 

| Variable | Purpose | Where to find it |
| -------- | ------- | ---------------- |
| application.secret | Part of the Ninja Framework in-built security | [Ninja Docs](http://www.ninjaframework.org/documentation/security/getting_started.html) |
| client_id | This Consumer Key value from your connected app | Setup -> Apps -> Your connected app |
| client_secret | This Consumer Secret value from your connected app | Setup -> Apps -> Your connected app |
| redirect_uri | This Callback URL value from your connected app | Setup -> Apps -> Your connected app | 
| REDIS_URL | The url for the configured redis server | Either in your local redis instance (http://localhost:6379 was my default) or will be configured in Heroku with the handy button below |
| SF_API_VERSION | This is the version number of the Salesforce API you want to use | as of writing the latest was 37.0 (notice there is no 'v' character) |

When you deploy using the Heroku button these variables will be created for you but you will need to populate the OAuth ones with your values to get this to work. 
When deploying locally, you need to set these on the environment so they can be accessed via a `System.getenv("property.name")` call. It's actually a little nicer than this using Ninja, you can [inject your properites file](http://www.ninjaframework.org/documentation/configuration_and_modes.html) and forget about it. Check out the cont/application.conf file in the codebase to see how the properties map.  

##Run Locally
If you want to run this project locally you will need a local version of Redis (or an accessible version of Redis somewhere)

This project uses the Lightning Design system, it is configured in the app.json so you might want to get the latest version and update. 
Current configuration is 2.1.3 of LDS. 
(FULL DISCLOSURE - I am rubbish with npm, so I can download and save but you will need to manually copy assets around )

Clone this repository then open the terminal.. (or actually, maybe better to [fork](https://help.github.com/articles/fork-a-repo/) then clone.. up to you)

I am going to document how I do this using Eclipse. 

```
> git clone https://github.com/ibigfoot/ninja-oauth.git

> mvn eclipse:eclipse

```
Open Eclipse and Import the existing project into your workspace.

![Import Project](/readme-images/importProject.png "Import Project")

Now create a new run configuration

![New Run Configuration](/readme-images/newRunConfiguration.png "New Remote Config")

Configure your environment variables

![Env Variables](/readme-images/configEnvVariables.png "Configure Environment")

Set your workspace and maven target of ninja:run 

![Set to Run](/readme-images/configNinjaRun.png "Set ninja:run")

You should see this output in the console

![Console Output](/readme-images/outputRunning.png "Expected Output")

## Heroku

This is where it gets easy.. push this button.

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

Gotta love that!

## What's next
Now you have your working Heroku app you probably have a couple of options on how to push code to it. 
1) Add a new git remote for your newly created heroku repo and simply git push heroku master
2) Create your own Github of this project and connect to use [Heroku Pipeline](https://devcenter.heroku.com/articles/pipelines) - much nicer experience 
3) get busy building an awesome app :) 



