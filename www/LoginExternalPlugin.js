var exec = require('cordova/exec');
/**
 *  The method Login is used to execute a Login in external Application
 * 
 *  @param {JSON} options - The options which wil be used to call an external application
 *      Example: 
 *      {
 *          "action": "com.outsystems.example.Example",
 *           "inputExtras": [
 *              {key:"key1",value: "A"},
                {key:"key2",value: "B"}
 *            ]
 *       }
 * @param {Function} success - The callback which will be called when switch to settings is successful.
 * @param {Function} error - The callback which will be called when switch to settings encounters an error.
 */
exports.login = function (options, success, error) {
    exec(success, error, 'LoginExternalPlugin', 'login', [options]);
};