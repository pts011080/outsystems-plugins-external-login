# Send Intent plugin

Cordova plugin to request a intent external in another application.

# API
Methods:

* **sendIntent**(options: JSON, success: function, error: function)
    * Call to request a intent external. Example:
    ```javascript
        cordova.plugins.SendIntentPlugin.sendIntent(
              [{
                "action": "com.outsystems.example.Example.Login",
                "inputExtras": [
                    {key:"key1",value: "A", type: "string"},
                    {key:"key2",value: false, type: "bool"}
                 ]
            }],
            function(success){console.log(success)},
            function(error){console.log(error)});
    ```
  - In additional, *inputExtras* attribute the types accepted is: "bool", "long", "double", "int" and "string".
    
