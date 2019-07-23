var exec = require('cordova/exec');
/**
 *  The method sent intent is used to execute an external intent in another Application
 * 
 *  @param {JSON} options - The options which wil be used to call an external application
 *      Example: 
 *      {
 *          "action": "com.outsystems.example.Example.Login",
 *          inputExtras : [
 *              {key:"key1",value: "A", type: "string"},
                {key:"key2",value: true, type: "bool"}
 *            ]
 *       }
 * @param {Function} success - The callback which will be called when switch to settings is successful.
 * @param {Function} error - The callback which will be called when switch to settings encounters an error.
 */
exports.sendIntent = function (options, success, error) {
    exec(success, error, 'SendIntentPlugin', 'sendIntent', [options]);
};