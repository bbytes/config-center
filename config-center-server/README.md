Config-Center
=============

  Configuration Center that can be accessed centrally and updated real time. The configuration tool that can be accessed centrally by java and spring framework apps. Control all application properties over the web in a secure way .
We can modify the application/project property and view the impact real time . This setup will be helpful if we run multiple instance of our app in cloud and we need elastic scaling , in this scenario we can modify the app properties centrally and all running instance would pick up the app properties from the cloud config manager without restarting the app.


![enter image description here][1]

The basic structure of application/project property

      Project
            Environment
                      property 1
                      property 2
                      property 3
            
            
Example structure

      Project - bank-app
            Environmant - dev 
                      property 1 - db url
                      property 2 - db username
                      property 3 - db password
            Environment - prod
                      property 1 - db url
                      property 2 - db username
                      property 3 - db password
                      



  [1]: http://snag.gy/HQ5Dm.jpg
