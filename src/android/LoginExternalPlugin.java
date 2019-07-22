package com.outsystems.loginexternalplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The login external plugin implemented for Android.
 * 
 */
public class LoginExternalPlugin extends CordovaPlugin {

    private static final String TAG = "LoginExternalPlugin";
    private CallbackContext callbackContext;
    private static final int LOGIN_SUCCESS_CODE = 9001;

    private static final String ACTION = "action";
    private static final String INPUT_EXTRAS = "inputExtras";
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Executes the request and returns JSONObject
     *
     * @param action            The action to execute.
     * @param args              JSONArray used to call another application with some parameters.
     * @param callbackContext   The callback context used when calling back into JavaScript.
     * @return                  True when the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action != null) {

            switch (Actions.getActionByName(action)) {
                case LOGIN:
                    requestExternalLogin(args);
                    PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
                    pr.setKeepCallback(true);
                    callbackContext.sendPluginResult(pr);
                    return true;
                case INVALID:
                    callbackContext.error(Actions.INVALID.getDescription());
                    return false;

            }
        } else {
            Log.v(TAG, Actions.INVALID.getDescription());
            callbackContext.error(Actions.INVALID.getDescription());
            return false;
        }

        return true;
    }

    /**
     * The login method in another Application
     *
     * @param args some args to use in another Application
     */
    private void login(JSONArray args) throws JSONException {
        JSONObject object = args.getJSONObject(0);

        String action = object.getString(ACTION);
        JSONArray inputExtras = object.getJSONArray(INPUT_EXTRAS);

        Intent intentLogin = new Intent(action);
        intentLogin.setFlags(0);

        if (inputExtras != null && args.length() > 0) {
            for (int i = 0; i < args.length(); i++) {

                JSONObject obj = args.getJSONObject(i);

                String key = obj.getString("key");
                String value = obj.getString("value");

                intentLogin.putExtra(key, value);
            }
        }

        callAnotherApplication(intentLogin);
        PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
        pr.setKeepCallback(true);
        callbackContext.sendPluginResult(pr);
    }

    private void callAnotherApplication(Intent intentLogin) {
        this.cordova.getActivity().startActivityForResult(intentLogin, LOGIN_SUCCESS_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK || requestCode == LOGIN_SUCCESS_CODE) {
            if (intent.getExtras() != null && intent.getExtras().get(ACCESS_TOKEN) != null) {
                String accessToken = (String) intent.getExtras().get(ACCESS_TOKEN);
                if (accessToken != null) {
                    JSONObject response = new JSONObject();
                    try {
                        response.put(ACCESS_TOKEN, accessToken);
                        callbackContext.success(response);
                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                        callbackContext.error("Error to get the access_token value in another application.");
                    }
                } else {
                    callbackContext.error("Error to get the access_token value in another application.");
                }
            }
        }
    }

    private enum Actions {

        LOGIN("login", "Login action using an external application"),
        INVALID("", "Invalid or not found action!");

        private String action;
        private String description;

        Actions(String action, String description) {
            this.action = action;
            this.description = description;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static Actions getActionByName(String action) {
            for (Actions a : Actions.values()) {
                if (a.action.equalsIgnoreCase(action)) {
                    return a;
                }
            }
            return INVALID;
        }
    }
}
