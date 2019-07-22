# External login plugin 

Cordova plugin to request a Login External in another applicatication.

# API
Methods:

* **login**(options: JSON, success: function, error: function)
    * Call to request a Login External. Example: 
    ```javascript
        cordova.plugins.LoginExternalPlugin.login(
              [{
                "action": "com.outsystems.example.Login",
                 "inputExtras": [
                    {key:"key1",value: "A"},
                    {key:"key2",value: "B"}
                 ]
            }],
            function(success){console.log(success),
            function(error){console.log(error)});
    ```
    
