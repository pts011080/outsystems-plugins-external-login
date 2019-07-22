# Send Intent plugin

Cordova plugin to request a intent external in another application.

# API
Methods:

* **sendIntent**(options: JSON, success: function, error: function)
    * Call to request a intent to login external. Example:
    ```javascript
        cordova.plugins.SendIntentExternalPlugin.sendIntent(
              [{
                "action": "Login",
                "package": "com.outsystems.example.Example",
                "inputExtras": [
                    {key:"key1",value: "A"},
                    {key:"key2",value: "B"}
                 ]
            }],
            function(success){console.log(success)},
            function(error){console.log(error)});
    ```
    
